package ng.cove.web.data.repo

import ng.cove.web.data.model.Levy
import org.springframework.data.mongodb.repository.MongoRepository

interface LevyRepo : MongoRepository<Levy, String>