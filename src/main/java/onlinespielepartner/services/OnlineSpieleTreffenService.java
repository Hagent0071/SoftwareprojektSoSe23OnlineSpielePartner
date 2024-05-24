package onlinespielepartner.services;

import java.util.Optional;
import onlinespielepartner.data.OnlineSpieleTreffen.OnlineSpieleTreffen;
import onlinespielepartner.data.OnlineSpieleTreffen.OnlineSpieleTreffenRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class OnlineSpieleTreffenService {

    private final OnlineSpieleTreffenRepository repository;

    public OnlineSpieleTreffenService(OnlineSpieleTreffenRepository repository) {
        this.repository = repository;
    }

    public Optional<OnlineSpieleTreffen> get(Long id) {
        return repository.findById(id);
    }

    public OnlineSpieleTreffen update(OnlineSpieleTreffen entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<OnlineSpieleTreffen> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<OnlineSpieleTreffen> list(Pageable pageable, Specification<OnlineSpieleTreffen> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
