package com.dongyun.cnucinema.spec.service;

import com.dongyun.cnucinema.spec.entity.Customer;
import com.dongyun.cnucinema.spec.entity.Schedule;
import com.dongyun.cnucinema.spec.entity.Ticketing;

public interface MailService {

    void sendTicketingMail(Customer customer, Schedule schedule, Ticketing ticketing);
}
