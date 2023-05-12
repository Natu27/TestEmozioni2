package backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@ComponentScan
public class CanzoneService {
    @Autowired
    private CanzoneRepository songRepository;

    public CanzoneService(CanzoneRepository repo) {
        songRepository = repo;
    }

    public List<Canzone> findAll(){
        return songRepository.findAll();
    }
}
