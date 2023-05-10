package backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CanzoneService {
    @Autowired
    CanzoneRepository songRepository;

    public List<Canzone> findAll() {
        return songRepository.findAll();
    }

    public void save(Canzone song) {
        songRepository.save(song);
    }

    public void delete(Canzone song) {
        songRepository.delete(song);
    }
}
