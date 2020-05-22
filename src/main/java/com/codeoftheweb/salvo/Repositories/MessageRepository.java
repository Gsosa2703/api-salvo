package com.codeoftheweb.salvo.repositories;

import com.codeoftheweb.salvo.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface MessageRepository extends JpaRepository<Message, Long> {

}
