const express = require("express");
const fs = require("fs");
const path = require("path");
const os = require("os");
const { ESLint } = require("eslint");

const app = express();
app.use(express.json());

const PORT = 5002;

app.post("/check", async (req, res) => {
    const { code } = req.body;

    if (!code || !code.trim()) {
        return res.json({
            violations: [{
                type: "LINT",
                message: "Code is empty",
                line: 0,
                severity: "ERROR"
            }]
        });
    }

    try {
        const violations = await runESLint(code);
        return res.json({ violations });
    } catch (err) {
        return res.json({
            violations: [{
                type: "SYSTEM",
                message: `ESLint analysis failed: ${err.message}`,
                line: 0,
                severity: "WARNING"
            }]
        });
    }
});

async function runESLint(code) {
    const tempDir = os.tmpdir();
    const tempFile = path.join(tempDir, `vericode_${Date.now()}.js`);
    const violations = [];

    try {
        fs.writeFileSync(tempFile, code);

        const eslint = new ESLint({
            overrideConfigFile: path.join(__dirname, ".eslintrc.json"),
            useEslintrc: false,
        });

        const results = await eslint.lintFiles([tempFile]);

        for (const result of results) {
            for (const msg of result.messages) {
                violations.push({
                    type: mapCategory(msg),
                    message: msg.message,
                    line: msg.line || 0,
                    severity: mapSeverity(msg.severity),
                });
            }
        }
    } finally {
        if (fs.existsSync(tempFile)) {
            fs.unlinkSync(tempFile);
        }
    }

    return violations;
}

function mapCategory(msg) {
    const ruleId = msg.ruleId || "";
    if (ruleId.includes("no-eval") || ruleId.includes("no-implied-eval")) {
        return "SECURITY";
    }
    if (ruleId.includes("semi") || ruleId.includes("curly") || ruleId.includes("eqeqeq")
        || ruleId.includes("prefer-const") || ruleId.includes("no-var")) {
        return "STYLE";
    }
    return "LINT";
}

function mapSeverity(eslintSeverity) {
    switch (eslintSeverity) {
        case 2: return "ERROR";
        case 1: return "WARNING";
        default: return "INFO";
    }
}

app.listen(PORT, () => {
    console.log(`JavaScript checker microservice running on port ${PORT}...`);
});
