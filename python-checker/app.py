"""
Flask microservice for Python code analysis using Pylint.
Runs on port 5001. Receives Python code via POST /check,
writes it to a temp file, runs Pylint, and returns violations as JSON.
"""

import json
import os
import tempfile
import subprocess
from flask import Flask, request, jsonify

app = Flask(__name__)


@app.route("/check", methods=["POST"])
def check_code():
    data = request.get_json()

    if not data or "code" not in data:
        return jsonify({"error": "Missing 'code' field in request body"}), 400

    code = data["code"]
    if not code or not code.strip():
        return jsonify({
            "violations": [{
                "type": "LINT",
                "message": "Code is empty",
                "line": 0,
                "severity": "ERROR"
            }]
        }), 200

    violations = run_pylint(code)
    return jsonify({"violations": violations}), 200


def run_pylint(code):
    """Write code to a temp file, run Pylint, parse and return violations."""
    violations = []
    temp_path = None

    try:
        fd, temp_path = tempfile.mkstemp(suffix=".py", prefix="vericode_")
        with os.fdopen(fd, "w") as f:
            f.write(code)

        result = subprocess.run(
            [
                "pylint",
                temp_path,
                "--output-format=json2",
                "--disable=C0114,C0115,C0116",  # skip missing docstring warnings
                "--max-line-length=120",
            ],
            capture_output=True,
            text=True,
            timeout=30,
        )

        if result.stdout.strip():
            pylint_output = json.loads(result.stdout)
            messages = pylint_output.get("messages", [])

            for msg in messages:
                violations.append({
                    "type": map_category(msg.get("type", "")),
                    "message": msg.get("message", "Unknown issue"),
                    "line": msg.get("line", 0),
                    "severity": map_severity(msg.get("type", "")),
                })

    except subprocess.TimeoutExpired:
        violations.append({
            "type": "SYSTEM",
            "message": "Pylint analysis timed out after 30 seconds",
            "line": 0,
            "severity": "WARNING",
        })
    except json.JSONDecodeError:
        violations.append({
            "type": "SYSTEM",
            "message": "Failed to parse Pylint output",
            "line": 0,
            "severity": "WARNING",
        })
    except Exception as e:
        violations.append({
            "type": "SYSTEM",
            "message": f"Pylint analysis failed: {str(e)}",
            "line": 0,
            "severity": "WARNING",
        })
    finally:
        if temp_path and os.path.exists(temp_path):
            os.remove(temp_path)

    return violations


def map_category(pylint_type):
    """Map Pylint message types to our violation categories."""
    mapping = {
        "convention": "STYLE",
        "refactor": "STYLE",
        "warning": "LINT",
        "error": "LINT",
        "fatal": "LINT",
    }
    return mapping.get(pylint_type, "LINT")


def map_severity(pylint_type):
    """Map Pylint message types to our severity levels."""
    mapping = {
        "convention": "INFO",
        "refactor": "WARNING",
        "warning": "WARNING",
        "error": "ERROR",
        "fatal": "ERROR",
    }
    return mapping.get(pylint_type, "WARNING")


if __name__ == "__main__":
    print("Starting Python checker microservice on port 5001...")
    app.run(host="0.0.0.0", port=5001, debug=True)
