package com.koreanair.reservation.control;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.koreanair.reservation.domain.bus.BusCity;
import com.koreanair.reservation.domain.bus.BusSchedule;
import com.koreanair.reservation.domain.bus.BusSeat;
import com.koreanair.reservation.domain.bus.BusSeatLayout;
import com.koreanair.reservation.domain.bus.BusTicket;
import com.koreanair.reservation.domain.passenger.Passenger;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Ticket;

/**
 * 대한항공 연계 우등고속 프리미엄 셔틀 발권 서비스.
 *
 * <p>Iteration 4 정정: 6대도시(집) → 인천공항 셔틀. 좌석 선택 가능.
 * 도시별 운행 스케줄 in-memory 카탈로그 보유.
 */
public class BusTicketingService {

    private final Map<BusCity, List<BusSchedule>> schedulesByCity = new HashMap<>();
    private final List<BusTicket> issuedTickets = new ArrayList<>();

    public BusTicketingService() {
        seedDefaultSchedules();
    }

    private void seedDefaultSchedules() {
        LocalDateTime today = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        int scheduleSeq = 1;
        for (BusCity city : BusCity.values()) {
            List<BusSchedule> list = new ArrayList<>();
            int[] hours = {5, 8, 12, 17};
            for (int h : hours) {
                LocalDateTime dep = today.withHour(h);
                LocalDateTime arr = dep.plusHours(travelHours(city));
                String id = String.format("BUS-%s-%03d", city.getCityCode(), scheduleSeq++);
                list.add(new BusSchedule(id, city, dep, arr, BusSeatLayout.standardPremium()));
            }
            schedulesByCity.put(city, list);
        }
    }

    private int travelHours(BusCity city) {
        switch (city) {
            case SEOUL: return 1;
            case INCHEON: return 1;
            case DAEJEON: return 3;
            case DAEGU: return 4;
            case GWANGJU: return 4;
            case BUSAN: return 5;
            default: return 3;
        }
    }

    public List<BusCity> supportedCities() {
        return List.of(BusCity.values());
    }

    public List<BusSchedule> schedulesFor(BusCity city) {
        return schedulesByCity.getOrDefault(city, Collections.emptyList());
    }

    public BusTicket issuePremiumTicket(Reservation reservation,
                                        Ticket airTicket,
                                        BusCity originCity,
                                        BusSchedule schedule,
                                        BusSeat seat) {
        if (reservation == null || airTicket == null || originCity == null) {
            throw new IllegalArgumentException("Reservation, airTicket, originCity는 필수입니다.");
        }
        BusSchedule chosen = schedule;
        BusSeat chosenSeat = seat;
        if (chosen == null) {
            List<BusSchedule> list = schedulesFor(originCity);
            if (!list.isEmpty()) {
                chosen = list.get(0);
            }
        }
        if (chosenSeat == null && chosen != null) {
            List<BusSeat> avail = chosen.availableSeats();
            if (!avail.isEmpty()) {
                chosenSeat = avail.get(0);
            }
        }
        if (chosenSeat != null) {
            chosenSeat.occupy(reservation.getPnrNumber());
        }
        Passenger passenger = airTicket.getPassenger();
        String passengerName = passenger != null ? passenger.getName() : "(unknown)";
        BusTicket busTicket = BusTicket.issue(
                reservation.getPnrNumber(),
                airTicket.getTicketNumber(),
                passengerName,
                originCity,
                chosen,
                chosenSeat);
        issuedTickets.add(busTicket);
        System.out.printf("[BUS] premium shuttle %s issued: %s→ICN pnr=%s seat=%s fare=%,d%n",
                busTicket.getTicketNumber(),
                originCity.getDisplayName(),
                reservation.getPnrNumber(),
                chosenSeat != null ? chosenSeat.getSeatNumber() : "-",
                busTicket.getFare());
        return busTicket;
    }

    public List<BusTicket> getIssuedTickets() {
        return Collections.unmodifiableList(issuedTickets);
    }
}
