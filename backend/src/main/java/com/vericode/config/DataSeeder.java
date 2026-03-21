package com.vericode.config;

import com.vericode.model.Language;
import com.vericode.model.PRStatus;
import com.vericode.model.PullRequest;
import com.vericode.repository.PullRequestRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PullRequestRepository pullRequestRepository;

    public DataSeeder(PullRequestRepository pullRequestRepository) {
        this.pullRequestRepository = pullRequestRepository;
    }

    @Override
    public void run(String... args) {
        // Only seed if the table is empty
        if (pullRequestRepository.count() > 0) {
            System.out.println("Data already exists, skipping seeding.");
            return;
        }

        PullRequest pr1 = new PullRequest(
                "Fix login null pointer",
                "shreya",
                Language.JAVA,
                "public class LoginService {\n    public User login(String email) {\n        return userRepo.findByEmail(email);\n    }\n}",
                "Fixes NPE when user email is not found",
                PRStatus.DRAFT
        );

        PullRequest pr2 = new PullRequest(
                "Add input validation",
                "keya",
                Language.PYTHON,
                "def validate_input(data):\n    if not data:\n        raise ValueError(\"Empty input\")\n    return True",
                "Adds basic input validation to API endpoints",
                PRStatus.IN_REVIEW
        );

        PullRequest pr3 = new PullRequest(
                "Refactor button component",
                "arundhati",
                Language.JAVASCRIPT,
                "const Button = ({ label, onClick }) => {\n    return <button onClick={onClick}>{label}</button>;\n};",
                "Cleans up the reusable button component",
                PRStatus.DRAFT
        );

        pullRequestRepository.save(pr1);
        pullRequestRepository.save(pr2);
        pullRequestRepository.save(pr3);

        System.out.println("Seeded 3 pull requests.");
    }
}