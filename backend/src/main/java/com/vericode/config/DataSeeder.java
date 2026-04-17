package com.vericode.config;

import com.vericode.model.Language;
import com.vericode.model.PRStatus;
import com.vericode.model.PullRequest;
import com.vericode.model.User;
import com.vericode.repository.PullRequestRepository;
import com.vericode.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PullRequestRepository pullRequestRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataSeeder(UserRepository userRepository, PullRequestRepository pullRequestRepository) {
        this.userRepository = userRepository;
        this.pullRequestRepository = pullRequestRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            System.out.println("Data already exists, skipping seeding.");
            return;
        }

        // Seed users
        User shreya = userRepository.save(new User("shreya", "Shreya Wanisha",
                "shreya@vericode.com", passwordEncoder.encode("password123")));
        User keya = userRepository.save(new User("keya", "Keya Goswami",
                "keya@vericode.com", passwordEncoder.encode("password123")));
        User arundhati = userRepository.save(new User("arundhati", "Arundhati Bandopadhyaya",
                "arundhati@vericode.com", passwordEncoder.encode("password123")));
        User Maitro = userRepository.save(new User("maitri", "Maitri Mukesh Pasale",
                "maitri@vericode.com", passwordEncoder.encode("password123")));

        // Seed PRs
        pullRequestRepository.save(new PullRequest(
                "Fix login null pointer", shreya, Language.JAVA,
                "public class LoginService {\n    public User login(String email) {\n        return userRepo.findByEmail(email);\n    }\n}",
                "Fixes NPE when user email is not found", PRStatus.DRAFT));

        pullRequestRepository.save(new PullRequest(
                "Add input validation", keya, Language.PYTHON,
                "def validate_input(data):\n    if not data:\n        raise ValueError(\"Empty input\")\n    return True",
                "Adds basic input validation to API endpoints", PRStatus.IN_REVIEW));

        pullRequestRepository.save(new PullRequest(
                "Refactor button component", arundhati, Language.JAVASCRIPT,
                "const Button = ({ label, onClick }) => {\n    return <button onClick={onClick}>{label}</button>;\n};",
                "Cleans up the reusable button component", PRStatus.DRAFT));

        System.out.println("Seeded 3 users and 3 pull requests.");
    }
}