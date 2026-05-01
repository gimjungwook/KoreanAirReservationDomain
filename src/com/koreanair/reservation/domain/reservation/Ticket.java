package com.koreanair.reservation.domain.reservation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.koreanair.reservation.domain.flight.Seat;
import com.koreanair.reservation.domain.passenger.Passenger;

/**
 * Ticket — e-Ticket 도메인 객체.
 *
 * <p>Iteration 2 활성화: ConfirmedState.issueTicket() 가 이 클래스의 {@link #generate}
 * 정적 팩토리를 호출해 e-Ticket 번호 (KE-yyMMdd-NNNN) 를 발급한다.
 * Reservation 의 tickets 컬렉션에 등록된다.
 */
public class Ticket {

    /** e-Ticket 번호 4자리 시퀀스를 발급하기 위한 프로세스 내 카운터. */
    private static long ticketIdSequence = 0L;
    private static int ticketNumberSequence = 0;
    private static final DateTimeFormatter TICKET_DATE_FMT = DateTimeFormatter.ofPattern("yyMMdd");

    private Long ticketId;
    private String ticketNumber;
    private Passenger passenger;
    private TicketStatus status;
    private LocalDateTime issuedAt;
    private SeatAssignment seatAssignment;

    public Ticket(String ticketNumber, Passenger passenger) {
        this.ticketId = ++ticketIdSequence;
        this.ticketNumber = ticketNumber;
        this.passenger = passenger;
        this.status = TicketStatus.READY_FOR_ISSUE;
        this.issuedAt = null;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public SeatAssignment getSeatAssignment() {
        return seatAssignment;
    }

    public void assignSeat(SeatAssignment seatAssignment) {
        this.seatAssignment = seatAssignment;
    }

    public void issue() {
        this.status = TicketStatus.ISSUED;
        this.issuedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = TicketStatus.CANCELLED;
    }

    /**
     * Iteration 2: e-Ticket 발급 정적 팩토리.
     *
     * <p>티켓 번호 포맷은 {@code KE-yyyyMMdd-NNNN}. NNNN 은 프로세스 내 4자리 시퀀스.
     * Seat 가 주어지면 SeatAssignment 를 만들어 ticket 에 연결한다.
     * 마지막에 {@link #issue()} 를 호출해 ISSUED 상태로 전환한다.
     */
    public static Ticket generate(Reservation reservation, Passenger passenger, Seat seat) {
        String today = LocalDateTime.now().format(TICKET_DATE_FMT);
        int seq = ++ticketNumberSequence;
        String ticketNumber = String.format("KE-%s-%04d", today, seq);
        Ticket ticket = new Ticket(ticketNumber, passenger);
        if (seat != null) {
            SeatAssignment assignment = new SeatAssignment();
            // Iter 2 simplicity: SeatAssignment 의 seat 필드는 changeSeat 로 주입.
            assignment.changeSeat(seat);
            ticket.assignSeat(assignment);
        }
        ticket.issue();
        return ticket;
    }

    /**
     * PNR 로 첫 번째 Ticket 을 조회한다. 일치하는 Reservation 이 없거나
     * 발급된 ticket 이 없으면 null.
     */
    public static Ticket getByReservation(String pnr) {
        Reservation reservation = Reservation.findByPnr(pnr);
        if (reservation == null) {
            return null;
        }
        for (Ticket t : reservation.getTickets()) {
            if (t != null) {
                return t;
            }
        }
        return null;
    }
}
