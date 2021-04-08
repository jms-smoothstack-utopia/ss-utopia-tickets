package com.ss.utopia.tickets.repository;

import com.ss.utopia.tickets.entity.Ticket;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketsRepository extends JpaRepository<Ticket, Long> {

  List<Ticket> findByPurchaserId(UUID purchaserId);
}
