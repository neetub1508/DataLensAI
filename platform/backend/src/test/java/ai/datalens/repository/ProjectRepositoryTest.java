package ai.datalens.repository;

import ai.datalens.entity.Project;
import ai.datalens.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    private User testUser1;
    private User testUser2;
    private Project activeProject1;
    private Project activeProject2;
    private Project inactiveProject;

    @BeforeEach
    void setUp() {
        // Create test users
        testUser1 = new User();
        testUser1.setEmail("user1@example.com");
        testUser1.setPasswordHash("hashedPassword1");
        testUser1 = entityManager.persistAndFlush(testUser1);

        testUser2 = new User();
        testUser2.setEmail("user2@example.com");
        testUser2.setPasswordHash("hashedPassword2");
        testUser2 = entityManager.persistAndFlush(testUser2);

        // Create test projects for user1
        activeProject1 = createProject("Active Project 1", "Description 1", true, testUser1);
        activeProject2 = createProject("Active Project 2", "Description 2", true, testUser1);
        inactiveProject = createProject("Inactive Project", "Description 3", false, testUser1);

        // Create test project for user2
        createProject("User2 Project", "User2 Description", true, testUser2);

        entityManager.flush();
        entityManager.clear();
    }

    private Project createProject(String name, String description, boolean isActive, User user) {
        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setIsActive(isActive);
        project.setUpdateDate(LocalDateTime.now());
        project.setUpdateBy(user.getId());
        project.setUser(user);
        return entityManager.persistAndFlush(project);
    }

    // POSITIVE TEST CASES

    @Test
    void findByUser_Success() {
        // When
        List<Project> projects = projectRepository.findByUser(testUser1);

        // Then
        assertThat(projects).hasSize(3);
        assertThat(projects).extracting(Project::getName)
                .containsExactlyInAnyOrder("Active Project 1", "Active Project 2", "Inactive Project");
    }

    @Test
    void findByUser_WithPagination_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<Project> projectPage = projectRepository.findByUser(testUser1, pageable);

        // Then
        assertThat(projectPage.getContent()).hasSize(2);
        assertThat(projectPage.getTotalElements()).isEqualTo(3);
        assertThat(projectPage.getTotalPages()).isEqualTo(2);
    }

    @Test
    void findByUserId_Success() {
        // When
        List<Project> projects = projectRepository.findByUserId(testUser1.getId());

        // Then
        assertThat(projects).hasSize(3);
        assertThat(projects).allMatch(project -> project.getUser().getId().equals(testUser1.getId()));
    }

    @Test
    void findByUserId_WithPagination_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<Project> projectPage = projectRepository.findByUserId(testUser1.getId(), pageable);

        // Then
        assertThat(projectPage.getContent()).hasSize(2);
        assertThat(projectPage.getTotalElements()).isEqualTo(3);
    }

    @Test
    void findByUserAndNameContainingIgnoreCase_Success() {
        // When
        List<Project> projects = projectRepository.findByUserAndNameContainingIgnoreCase(testUser1, "Project 1");

        // Then
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getName()).isEqualTo("Active Project 1");
    }

    @Test
    void findByUserAndNameContainingIgnoreCase_WithPagination_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 1);

        // When - Search for "Project" which will match all 3 projects
        Page<Project> projectPage = projectRepository.findByUserAndNameContainingIgnoreCase(testUser1, "Project", pageable);

        // Then
        assertThat(projectPage.getContent()).hasSize(1);
        assertThat(projectPage.getTotalElements()).isEqualTo(3);
    }

    @Test
    void findByUserAndId_Success() {
        // When
        Optional<Project> project = projectRepository.findByUserAndId(testUser1, activeProject1.getId());

        // Then
        assertThat(project).isPresent();
        assertThat(project.get().getName()).isEqualTo("Active Project 1");
    }

    @Test
    void findByUserIdAndId_Success() {
        // When
        Optional<Project> project = projectRepository.findByUserIdAndId(testUser1.getId(), activeProject1.getId());

        // Then
        assertThat(project).isPresent();
        assertThat(project.get().getName()).isEqualTo("Active Project 1");
    }

    @Test
    void existsByUserAndNameIgnoreCase_ExistingProject_ReturnsTrue() {
        // When
        boolean exists = projectRepository.existsByUserAndNameIgnoreCase(testUser1, "ACTIVE PROJECT 1");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByUserAndNameIgnoreCase_NonExistingProject_ReturnsFalse() {
        // When
        boolean exists = projectRepository.existsByUserAndNameIgnoreCase(testUser1, "Non Existing Project");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void countByUser_Success() {
        // When
        long count = projectRepository.countByUser(testUser1);

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    void countByUserId_Success() {
        // When
        long count = projectRepository.countByUserId(testUser1.getId());

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    void findByUserOrderByCreatedAtDesc_Success() {
        // When
        List<Project> projects = projectRepository.findByUserOrderByCreatedAtDesc(testUser1);

        // Then
        assertThat(projects).hasSize(3);
        // Projects should be ordered by creation date in descending order
        assertThat(projects.get(0).getCreatedAt()).isAfterOrEqualTo(projects.get(1).getCreatedAt());
        assertThat(projects.get(1).getCreatedAt()).isAfterOrEqualTo(projects.get(2).getCreatedAt());
    }

    @Test
    void findByUserOrderByCreatedAtDesc_WithPagination_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<Project> projectPage = projectRepository.findByUserOrderByCreatedAtDesc(testUser1, pageable);

        // Then
        assertThat(projectPage.getContent()).hasSize(2);
        assertThat(projectPage.getTotalElements()).isEqualTo(3);
    }

    @Test
    void findRecentProjectsByUserId_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<Project> projectPage = projectRepository.findRecentProjectsByUserId(testUser1.getId(), pageable);

        // Then
        assertThat(projectPage.getContent()).hasSize(2);
        assertThat(projectPage.getTotalElements()).isEqualTo(3);
    }

    @Test
    void findByUserAndIsActiveTrue_Success() {
        // When
        List<Project> activeProjects = projectRepository.findByUserAndIsActiveTrue(testUser1);

        // Then
        assertThat(activeProjects).hasSize(2);
        assertThat(activeProjects).allMatch(Project::getIsActive);
        assertThat(activeProjects).extracting(Project::getName)
                .containsExactlyInAnyOrder("Active Project 1", "Active Project 2");
    }

    @Test
    void findByUserAndIsActiveTrue_WithPagination_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 1);

        // When
        Page<Project> projectPage = projectRepository.findByUserAndIsActiveTrue(testUser1, pageable);

        // Then
        assertThat(projectPage.getContent()).hasSize(1);
        assertThat(projectPage.getTotalElements()).isEqualTo(2);
        assertThat(projectPage.getContent().get(0).getIsActive()).isTrue();
    }

    @Test
    void findByUserAndIsActive_ActiveProjects_Success() {
        // When
        List<Project> activeProjects = projectRepository.findByUserAndIsActive(testUser1, true);

        // Then
        assertThat(activeProjects).hasSize(2);
        assertThat(activeProjects).allMatch(Project::getIsActive);
    }

    @Test
    void findByUserAndIsActive_InactiveProjects_Success() {
        // When
        List<Project> inactiveProjects = projectRepository.findByUserAndIsActive(testUser1, false);

        // Then
        assertThat(inactiveProjects).hasSize(1);
        assertThat(inactiveProjects.get(0).getName()).isEqualTo("Inactive Project");
        assertThat(inactiveProjects.get(0).getIsActive()).isFalse();
    }

    @Test
    void findByUserAndIsActive_WithPagination_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 1);

        // When
        Page<Project> projectPage = projectRepository.findByUserAndIsActive(testUser1, true, pageable);

        // Then
        assertThat(projectPage.getContent()).hasSize(1);
        assertThat(projectPage.getTotalElements()).isEqualTo(2);
        assertThat(projectPage.getContent().get(0).getIsActive()).isTrue();
    }

    @Test
    void countByUserAndIsActiveTrue_Success() {
        // When
        long count = projectRepository.countByUserAndIsActiveTrue(testUser1);

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void countByUserAndIsActive_Success() {
        // When
        long activeCount = projectRepository.countByUserAndIsActive(testUser1, true);
        long inactiveCount = projectRepository.countByUserAndIsActive(testUser1, false);

        // Then
        assertThat(activeCount).isEqualTo(2);
        assertThat(inactiveCount).isEqualTo(1);
    }

    @Test
    void findByUpdateBy_Success() {
        // When
        List<Project> projects = projectRepository.findByUpdateBy(testUser1.getId());

        // Then
        assertThat(projects).hasSize(3);
        assertThat(projects).allMatch(project -> project.getUpdateBy().equals(testUser1.getId()));
    }

    @Test
    void findByUserAndUpdateDateAfter_Success() {
        // Given
        LocalDateTime cutoffDate = LocalDateTime.now().minusHours(1);

        // When
        List<Project> recentProjects = projectRepository.findByUserAndUpdateDateAfter(testUser1, cutoffDate);

        // Then
        assertThat(recentProjects).hasSize(3);
        assertThat(recentProjects).allMatch(project -> project.getUpdateDate().isAfter(cutoffDate));
    }

    @Test
    void findActiveProjectsByUserOrderByUpdateDateDesc_Success() {
        // When
        List<Project> activeProjects = projectRepository.findActiveProjectsByUserOrderByUpdateDateDesc(testUser1);

        // Then
        assertThat(activeProjects).hasSize(2);
        assertThat(activeProjects).allMatch(Project::getIsActive);
        assertThat(activeProjects).extracting(Project::getName)
                .containsExactlyInAnyOrder("Active Project 1", "Active Project 2");
    }

    @Test
    void findActiveProjectsByUserOrderByUpdateDateDesc_WithPagination_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 1);

        // When
        Page<Project> projectPage = projectRepository.findActiveProjectsByUserOrderByUpdateDateDesc(testUser1, pageable);

        // Then
        assertThat(projectPage.getContent()).hasSize(1);
        assertThat(projectPage.getTotalElements()).isEqualTo(2);
        assertThat(projectPage.getContent().get(0).getIsActive()).isTrue();
    }

    @Test
    void findAllWithUser_Success() {
        // When
        List<Project> allProjects = projectRepository.findAllWithUser();

        // Then
        assertThat(allProjects).hasSize(4); // 3 from user1 + 1 from user2
        assertThat(allProjects).allMatch(project -> project.getUser() != null);
    }

    // NEGATIVE TEST CASES

    @Test
    void findByUser_UserWithNoProjects_ReturnsEmpty() {
        // Given
        User userWithNoProjects = new User();
        userWithNoProjects.setEmail("noprojects@example.com");
        userWithNoProjects.setPasswordHash("hashedPassword");
        userWithNoProjects = entityManager.persistAndFlush(userWithNoProjects);

        // When
        List<Project> projects = projectRepository.findByUser(userWithNoProjects);

        // Then
        assertThat(projects).isEmpty();
    }

    @Test
    void findByUserAndId_DifferentUser_ReturnsEmpty() {
        // When
        Optional<Project> project = projectRepository.findByUserAndId(testUser2, activeProject1.getId());

        // Then
        assertThat(project).isEmpty();
    }

    @Test
    void findByUserIdAndId_DifferentUser_ReturnsEmpty() {
        // When
        Optional<Project> project = projectRepository.findByUserIdAndId(testUser2.getId(), activeProject1.getId());

        // Then
        assertThat(project).isEmpty();
    }

    @Test
    void findByUserAndId_NonExistentProject_ReturnsEmpty() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Optional<Project> project = projectRepository.findByUserAndId(testUser1, nonExistentId);

        // Then
        assertThat(project).isEmpty();
    }

    @Test
    void findByUserAndNameContainingIgnoreCase_NoMatches_ReturnsEmpty() {
        // When
        List<Project> projects = projectRepository.findByUserAndNameContainingIgnoreCase(testUser1, "NonExistent");

        // Then
        assertThat(projects).isEmpty();
    }

    @Test
    void existsByUserAndNameIgnoreCase_DifferentUser_ReturnsFalse() {
        // When
        boolean exists = projectRepository.existsByUserAndNameIgnoreCase(testUser2, "Active Project 1");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void countByUser_UserWithNoProjects_ReturnsZero() {
        // Given
        User userWithNoProjects = new User();
        userWithNoProjects.setEmail("noprojects@example.com");
        userWithNoProjects.setPasswordHash("hashedPassword");
        userWithNoProjects = entityManager.persistAndFlush(userWithNoProjects);

        // When
        long count = projectRepository.countByUser(userWithNoProjects);

        // Then
        assertThat(count).isZero();
    }

    @Test
    void findByUserAndIsActiveTrue_NoActiveProjects_ReturnsEmpty() {
        // Given - Create a user with only inactive projects
        User userWithInactiveProjects = new User();
        userWithInactiveProjects.setEmail("inactive@example.com");
        userWithInactiveProjects.setPasswordHash("hashedPassword");
        userWithInactiveProjects = entityManager.persistAndFlush(userWithInactiveProjects);

        createProject("Inactive Project 1", "Description", false, userWithInactiveProjects);
        createProject("Inactive Project 2", "Description", false, userWithInactiveProjects);

        // When
        List<Project> activeProjects = projectRepository.findByUserAndIsActiveTrue(userWithInactiveProjects);

        // Then
        assertThat(activeProjects).isEmpty();
    }

    @Test
    void findByUserAndIsActive_NoInactiveProjects_ReturnsEmpty() {
        // Given - Create a user with only active projects
        User userWithActiveProjects = new User();
        userWithActiveProjects.setEmail("activeonly@example.com");
        userWithActiveProjects.setPasswordHash("hashedPassword");
        userWithActiveProjects = entityManager.persistAndFlush(userWithActiveProjects);

        createProject("Active Project 1", "Description", true, userWithActiveProjects);

        // When
        List<Project> inactiveProjects = projectRepository.findByUserAndIsActive(userWithActiveProjects, false);

        // Then
        assertThat(inactiveProjects).isEmpty();
    }

    @Test
    void countByUserAndIsActiveTrue_NoActiveProjects_ReturnsZero() {
        // Given - Create a user with only inactive projects
        User userWithInactiveProjects = new User();
        userWithInactiveProjects.setEmail("inactive@example.com");
        userWithInactiveProjects.setPasswordHash("hashedPassword");
        userWithInactiveProjects = entityManager.persistAndFlush(userWithInactiveProjects);

        createProject("Inactive Project", "Description", false, userWithInactiveProjects);

        // When
        long count = projectRepository.countByUserAndIsActiveTrue(userWithInactiveProjects);

        // Then
        assertThat(count).isZero();
    }

    @Test
    void findByUpdateBy_NonExistentUser_ReturnsEmpty() {
        // Given
        UUID nonExistentUserId = UUID.randomUUID();

        // When
        List<Project> projects = projectRepository.findByUpdateBy(nonExistentUserId);

        // Then
        assertThat(projects).isEmpty();
    }

    @Test
    void findByUserAndUpdateDateAfter_FutureDate_ReturnsEmpty() {
        // Given
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);

        // When
        List<Project> projects = projectRepository.findByUserAndUpdateDateAfter(testUser1, futureDate);

        // Then
        assertThat(projects).isEmpty();
    }

    @Test
    void findActiveProjectsByUserOrderByUpdateDateDesc_NoActiveProjects_ReturnsEmpty() {
        // Given - Create a user with only inactive projects
        User userWithInactiveProjects = new User();
        userWithInactiveProjects.setEmail("inactive@example.com");
        userWithInactiveProjects.setPasswordHash("hashedPassword");
        userWithInactiveProjects = entityManager.persistAndFlush(userWithInactiveProjects);

        createProject("Inactive Project", "Description", false, userWithInactiveProjects);

        // When
        List<Project> activeProjects = projectRepository.findActiveProjectsByUserOrderByUpdateDateDesc(userWithInactiveProjects);

        // Then
        assertThat(activeProjects).isEmpty();
    }

    @Test
    void findRecentProjectsByUserId_WithSmallLimit_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 1);

        // When
        Page<Project> projectPage = projectRepository.findRecentProjectsByUserId(testUser1.getId(), pageable);

        // Then
        assertThat(projectPage.getContent()).hasSize(1);
        assertThat(projectPage.getTotalElements()).isEqualTo(3);
    }

    @Test
    void findByUser_WithLargePageNumber_ReturnsEmpty() {
        // Given
        Pageable pageable = PageRequest.of(10, 10); // Page that doesn't exist

        // When
        Page<Project> projectPage = projectRepository.findByUser(testUser1, pageable);

        // Then
        assertThat(projectPage.getContent()).isEmpty();
        assertThat(projectPage.getTotalElements()).isEqualTo(3);
        assertThat(projectPage.getTotalPages()).isEqualTo(1);
    }
}