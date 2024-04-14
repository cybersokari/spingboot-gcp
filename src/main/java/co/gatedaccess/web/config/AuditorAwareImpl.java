package co.gatedaccess.web.config;

import org.springframework.data.domain.AuditorAware;

import java.util.Date;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Date> {
    @Override
    public Optional<Date> getCurrentAuditor() {
        return Optional.of(new Date());
    }
}
