package co.gatedaccess.web.config

import org.springframework.data.domain.AuditorAware
import java.util.*

class AuditorAwareImpl : AuditorAware<Date> {
    override fun getCurrentAuditor(): Optional<Date> {
        return Optional.of(Date())
    }
}
