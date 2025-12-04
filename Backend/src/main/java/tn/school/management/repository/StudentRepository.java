package tn.school.management.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.school.management.entity.Level;
import tn.school.management.entity.Student;


@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByUsername(String username);

    Page<Student> findByLevel(Level level, Pageable pageable);

    Page<Student> findByUsernameContaining(String username, Pageable pageable);

}