package backend;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CanzoneRepository extends JpaRepository<Canzone, Integer> {

}
