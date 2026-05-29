package com.koreanair.reservation.app.sample;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.koreanair.reservation.control.AuthService;
import com.koreanair.reservation.control.FlightSearchService;
import com.koreanair.reservation.domain.flight.Airport;
import com.koreanair.reservation.domain.flight.AirportCatalog;
import com.koreanair.reservation.domain.flight.AirportCity;
import com.koreanair.reservation.domain.flight.BookingClass;
import com.koreanair.reservation.domain.flight.Fare;
import com.koreanair.reservation.domain.flight.FareRule;
import com.koreanair.reservation.domain.flight.Flight;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.flight.Route;
import com.koreanair.reservation.domain.user.Member;

public final class SampleData {

    private SampleData() {}

    public static Airport airport(String code, String name, String city, String country, String flag) {
        Airport a = new Airport();
        setField(a, "airportCode", code);
        setField(a, "airportName", flag + " " + name);
        setField(a, "city", city);
        setField(a, "country", country);
        return a;
    }

    public static FareRule fareRule(String fareClass, boolean refundable, long changeFee, long cancellationPenalty) {
        FareRule r = new FareRule();
        setField(r, "fareClass", fareClass);
        setField(r, "refundable", refundable);
        setField(r, "changeFee", BigDecimal.valueOf(changeFee));
        setField(r, "cancellationPenalty", BigDecimal.valueOf(cancellationPenalty));
        return r;
    }

    public static Fare fare(BookingClass bc, long basePrice, boolean refundable) {
        Fare f = new Fare();
        setField(f, "bookingClass", bc);
        setField(f, "basePrice", BigDecimal.valueOf(basePrice));
        setField(f, "refundable", refundable);
        return f;
    }

    public static Flight flight(String flightNumber, Airport origin, Airport dest, Fare... fares) {
        Route route = new Route();
        setField(route, "origin", origin);
        setField(route, "destination", dest);
        setField(route, "routeCode", origin != null ? origin.getAirportCode() + "-" + dest.getAirportCode() : null);
        Flight f = new Flight();
        setField(f, "flightNumber", flightNumber);
        setField(f, "route", route);
        for (Fare fare : fares) {
            f.addFare(fare);
        }
        return f;
    }

    public static FlightSchedule schedule(Flight flight, long scheduleId, LocalDateTime departure, LocalDateTime arrival, FareRule fareRule) {
        FlightSchedule s = new FlightSchedule();
        setField(s, "scheduleId", scheduleId);
        setField(s, "flight", flight);
        setField(s, "departureDateTime", departure);
        setField(s, "arrivalDateTime", arrival);
        setField(s, "status", com.koreanair.reservation.domain.flight.FlightStatus.SCHEDULED);
        s.setFareRule(fareRule);
        return s;
    }

    public static Member member(String name, String email, String memberNumber) {
        Member m = new Member();
        setField(m, "name", name);
        setField(m, "email", email);
        setField(m, "memberNumber", memberNumber);
        return m;
    }

    public static SeedResult seedAll(AuthService auth, FlightSearchService search) {
        Airport icn = airport("ICN", "Incheon International", "Seoul", "Korea", "🇰🇷");
        Airport nrt = airport("NRT", "Narita International", "Tokyo", "Japan", "🇯🇵");
        Airport lax = airport("LAX", "Los Angeles International", "Los Angeles", "USA", "🇺🇸");
        Airport gmp = airport("GMP", "Gimpo International", "Seoul", "Korea", "🇰🇷");
        Airport sin = airport("SIN", "Changi Airport", "Singapore", "Singapore", "🇸🇬");
        Airport hnd = airport("HND", "Haneda Airport", "Tokyo", "Japan", "🇯🇵");
        Airport fuk = airport("FUK", "Fukuoka Airport", "Fukuoka", "Japan", "🇯🇵");
        Airport bkk = airport("BKK", "Suvarnabhumi Airport", "Bangkok", "Thailand", "🇹🇭");
        Airport pvg = airport("PVG", "Pudong International", "Shanghai", "China", "🇨🇳");
        Airport hkg = airport("HKG", "Hong Kong International", "Hong Kong", "Hong Kong", "🇭🇰");
        Airport del = airport("DEL", "Indira Gandhi International", "Delhi", "India", "🇮🇳");
        Airport syd = airport("SYD", "Sydney Kingsford Smith", "Sydney", "Australia", "🇦🇺");
        Airport cdg = airport("CDG", "Charles de Gaulle", "Paris", "France", "🇫🇷");
        Airport fra = airport("FRA", "Frankfurt Airport", "Frankfurt", "Germany", "🇩🇪");
        Airport jfk = airport("JFK", "John F. Kennedy International", "New York", "USA", "🇺🇸");
        Airport lga = airport("LGA", "LaGuardia Airport", "New York", "USA", "🇺🇸");
        Airport ewr = airport("EWR", "Newark Liberty International", "New York", "USA", "🇺🇸");
        Airport fco = airport("FCO", "Leonardo da Vinci", "Rome", "Italy", "🇮🇹");
        Airport lhr = airport("LHR", "Heathrow Airport", "London", "UK", "🇬🇧");
        Airport lgw = airport("LGW", "Gatwick Airport", "London", "UK", "🇬🇧");
        Airport stn = airport("STN", "Stansted Airport", "London", "UK", "🇬🇧");
        Airport yyz = airport("YYZ", "Pearson International", "Toronto", "Canada", "🇨🇦");

        // DP#4 Composite — multi-airport metropolitan groups
        AirportCity nyc = new AirportCity("NYC", "New York", "USA").add(jfk).add(lga).add(ewr);
        AirportCity tyo = new AirportCity("TYO", "Tokyo", "Japan").add(nrt).add(hnd);
        AirportCity lon = new AirportCity("LON", "London", "UK").add(lhr).add(lgw).add(stn);
        AirportCity sel = new AirportCity("SEL", "Seoul", "Korea").add(icn).add(gmp);

        AirportCatalog cat = search.getAirportCatalog();
        cat.registerAirport(icn).registerAirport(nrt).registerAirport(lax).registerAirport(gmp)
                .registerAirport(sin).registerAirport(hnd).registerAirport(fuk).registerAirport(bkk)
                .registerAirport(pvg).registerAirport(hkg).registerAirport(del).registerAirport(syd)
                .registerAirport(cdg).registerAirport(fra).registerAirport(jfk).registerAirport(lga)
                .registerAirport(ewr).registerAirport(fco).registerAirport(lhr).registerAirport(lgw)
                .registerAirport(stn).registerAirport(yyz);
        cat.registerCity(nyc).registerCity(tyo).registerCity(lon).registerCity(sel);

        Fare ecoY = fare(BookingClass.Y, 320_000L, true);
        Fare ecoB = fare(BookingClass.B, 290_000L, false);
        Fare ecoM = fare(BookingClass.M, 260_000L, false);
        Fare busJ = fare(BookingClass.J, 580_000L, true);
        Fare fstF = fare(BookingClass.F, 1_250_000L, true);
        Fare premY = fare(BookingClass.Y, 480_000L, true);
        Fare bizK = fare(BookingClass.K, 720_000L, true);
        Fare ecoS = fare(BookingClass.Y, 180_000L, true);
        Fare ecoW = fare(BookingClass.B, 220_000L, false);

        FareRule yRule = fareRule("Y", true, 30_000L, 100_000L);
        FareRule bRule = fareRule("B", false, 50_000L, 150_000L);
        FareRule mRule = fareRule("M", false, 60_000L, 200_000L);
        FareRule jRule = fareRule("J", true, 20_000L, 80_000L);
        FareRule fRule = fareRule("F", true, 0L, 50_000L);
        FareRule kRule = fareRule("K", true, 10_000L, 30_000L);

        Flight k001 = flight("KE001", icn, nrt, ecoY, busJ);
        Flight k002 = flight("KE002", nrt, icn, ecoY, busJ);
        Flight k003 = flight("KE003", icn, nrt, ecoB, fstF);
        Flight k004 = flight("KE004", nrt, icn, ecoB, fstF);
        Flight k011 = flight("KE011", icn, hnd, ecoY, ecoB);
        Flight k012 = flight("KE012", hnd, icn, ecoY, ecoB);
        Flight k013 = flight("KE013", icn, hnd, ecoM, busJ);
        Flight k014 = flight("KE014", hnd, icn, ecoM, busJ);
        Flight k021 = flight("KE021", icn, fuk, ecoS, ecoW);
        Flight k022 = flight("KE022", fuk, icn, ecoS, ecoW);
        Flight k023 = flight("KE023", icn, fuk, ecoM, busJ);
        Flight k024 = flight("KE024", fuk, icn, ecoM, busJ);
        Flight k051 = flight("KE051", icn, sin, ecoY, busJ);
        Flight k052 = flight("KE052", sin, icn, ecoY, busJ);
        Flight k053 = flight("KE053", icn, sin, ecoB, fstF);
        Flight k054 = flight("KE054", sin, icn, ecoB, fstF);
        Flight k061 = flight("KE061", icn, bkk, ecoY, busJ);
        Flight k062 = flight("KE062", bkk, icn, ecoY, busJ);
        Flight k063 = flight("KE063", icn, bkk, ecoB, ecoY);
        Flight k064 = flight("KE064", bkk, icn, ecoB, ecoY);
        Flight k071 = flight("KE071", icn, hkg, ecoY, ecoB);
        Flight k072 = flight("KE072", hkg, icn, ecoY, ecoB);
        Flight k073 = flight("KE073", icn, hkg, ecoM, busJ);
        Flight k074 = flight("KE074", hkg, icn, ecoM, busJ);
        Flight k081 = flight("KE081", icn, pvg, ecoS, ecoW);
        Flight k082 = flight("KE082", pvg, icn, ecoS, ecoW);
        Flight k083 = flight("KE083", icn, pvg, ecoM, fstF);
        Flight k084 = flight("KE084", pvg, icn, ecoM, fstF);
        Flight k091 = flight("KE091", icn, del, ecoY, busJ);
        Flight k092 = flight("KE092", del, icn, ecoY, busJ);
        Flight k093 = flight("KE093", icn, del, ecoB, ecoY);
        Flight k094 = flight("KE094", del, icn, ecoB, ecoY);
        Flight k101 = flight("KE101", icn, lax, ecoY, busJ);
        Flight k102 = flight("KE102", lax, icn, ecoY, busJ);
        Flight k103 = flight("KE103", icn, lax, ecoB, ecoY);
        Flight k104 = flight("KE104", lax, icn, ecoB, ecoY);
        Flight k105 = flight("KE105", icn, lax, fstF, bizK);
        Flight k106 = flight("KE106", lax, icn, fstF, bizK);
        Flight k107 = flight("KE107", icn, lax, premY, fstF);
        Flight k108 = flight("KE108", lax, icn, premY, fstF);
        Flight k111 = flight("KE111", icn, jfk, ecoY, busJ);
        Flight k112 = flight("KE112", jfk, icn, ecoY, busJ);
        Flight k113 = flight("KE113", icn, jfk, ecoB, ecoY);
        Flight k114 = flight("KE114", jfk, icn, ecoB, ecoY);
        Flight k115 = flight("KE115", jfk, yyz, ecoY, ecoB);
        Flight k116 = flight("KE116", yyz, jfk, ecoY, ecoB);
        Flight k117 = flight("KE117", icn, jfk, ecoY, ecoB);
        Flight k118 = flight("KE118", jfk, icn, ecoY, ecoB);
        Flight k121 = flight("KE121", icn, syd, ecoY, busJ);
        Flight k122 = flight("KE122", syd, icn, ecoY, busJ);
        Flight k123 = flight("KE123", icn, syd, ecoB, fstF);
        Flight k124 = flight("KE124", syd, icn, ecoB, fstF);
        Flight k125 = flight("KE125", syd, icn, premY, fstF);
        Flight k126 = flight("KE126", syd, icn, premY, fstF);
        Flight k127 = flight("KE127", syd, icn, ecoY, busJ);
        Flight k131 = flight("KE131", icn, cdg, ecoY, busJ);
        Flight k132 = flight("KE132", cdg, icn, ecoY, busJ);
        Flight k133 = flight("KE133", icn, cdg, ecoB, fstF);
        Flight k134 = flight("KE134", cdg, icn, ecoB, fstF);
        Flight k135 = flight("KE135", icn, cdg, fstF, bizK);
        Flight k136 = flight("KE136", cdg, icn, fstF, bizK);
        Flight k137 = flight("KE137", cdg, icn, ecoY, busJ);
        Flight k141 = flight("KE141", icn, fra, ecoY, busJ);
        Flight k142 = flight("KE142", fra, icn, ecoY, busJ);
        Flight k143 = flight("KE143", icn, fra, ecoB, ecoY);
        Flight k144 = flight("KE144", fra, icn, ecoB, ecoY);
        Flight k151 = flight("KE151", lax, jfk, ecoY, ecoB);
        Flight k152 = flight("KE152", jfk, lax, ecoY, ecoB);
        Flight k153 = flight("KE153", lax, jfk, ecoB, ecoY);
        Flight k161 = flight("KE161", icn, lhr, ecoY, busJ);
        Flight k162 = flight("KE162", lhr, icn, ecoY, busJ);
        Flight k163 = flight("KE163", icn, lhr, ecoB, fstF);
        Flight k164 = flight("KE164", lhr, icn, ecoB, fstF);
        Flight k171 = flight("KE171", icn, fco, ecoY, busJ);
        Flight k172 = flight("KE172", fco, icn, ecoY, busJ);
        Flight k201 = flight("KE201", icn, gmp, ecoS, ecoW);
        Flight k202 = flight("KE202", gmp, icn, ecoS, ecoW);
        Flight k203 = flight("KE203", gmp, nrt, ecoS, ecoW);
        Flight k204 = flight("KE204", nrt, gmp, ecoS, ecoW);
        Flight k205 = flight("KE205", gmp, icn, ecoB, ecoY);
        Flight k206 = flight("KE206", icn, gmp, ecoB, ecoY);
        Flight k301 = flight("KE301", sin, lax, ecoY, busJ);
        Flight k302 = flight("KE302", lax, sin, ecoY, busJ);
        Flight k303 = flight("KE303", sin, lax, ecoB, fstF);
        Flight k304 = flight("KE304", lax, sin, ecoB, fstF);
        Flight k401 = flight("KE401", bkk, sin, ecoY, ecoB);
        Flight k402 = flight("KE402", sin, bkk, ecoY, ecoB);
        Flight k501 = flight("KE501", hkg, nrt, ecoY, ecoB);
        Flight k502 = flight("KE502", nrt, hkg, ecoY, ecoB);
        Flight k701 = flight("KE701", nrt, jfk, ecoY, busJ);
        Flight k702 = flight("KE702", jfk, lax, ecoY, ecoB);
        Flight k703 = flight("KE703", lax, cdg, ecoY, busJ);
        Flight k704 = flight("KE704", cdg, icn, ecoY, busJ);
        Flight k705 = flight("KE705", nrt, syd, ecoY, busJ);
        Flight k706 = flight("KE706", syd, sin, ecoY, ecoB);

        LocalDate d = LocalDate.now();
        LocalDate d1 = d.plusDays(1);
        LocalDate d2 = d.plusDays(2);
        LocalDate d3 = d.plusDays(3);
        LocalDate d4 = d.plusDays(4);
        LocalDate d5 = d.plusDays(5);
        LocalDate d6 = d.plusDays(6);
        LocalDate d7 = d.plusDays(7);

        int d1y = d1.getYear(), d1m = d1.getMonthValue(), d1day = d1.getDayOfMonth();
        int d2y = d2.getYear(), d2m = d2.getMonthValue(), d2day = d2.getDayOfMonth();
        int d3y = d3.getYear(), d3m = d3.getMonthValue(), d3day = d3.getDayOfMonth();
        int d4y = d4.getYear(), d4m = d4.getMonthValue(), d4day = d4.getDayOfMonth();
        int d5y = d5.getYear(), d5m = d5.getMonthValue(), d5day = d5.getDayOfMonth();
        int d6y = d6.getYear(), d6m = d6.getMonthValue(), d6day = d6.getDayOfMonth();
        int d7y = d7.getYear(), d7m = d7.getMonthValue(), d7day = d7.getDayOfMonth();

        search.addSchedule(schedule(k001, 1L, LocalDateTime.of(d1y, d1m, d1day, 9, 30), LocalDateTime.of(d1y, d1m, d1day, 12, 55), yRule));
        search.addSchedule(schedule(k002, 2L, LocalDateTime.of(d1y, d1m, d1day, 14, 30), LocalDateTime.of(d1y, d1m, d1day, 17, 55), yRule));
        search.addSchedule(schedule(k003, 3L, LocalDateTime.of(d1y, d1m, d1day, 17, 0), LocalDateTime.of(d1y, d1m, d1day, 20, 25), jRule));
        search.addSchedule(schedule(k011, 4L, LocalDateTime.of(d1y, d1m, d1day, 8, 0), LocalDateTime.of(d1y, d1m, d1day, 10, 15), yRule));
        search.addSchedule(schedule(k012, 5L, LocalDateTime.of(d1y, d1m, d1day, 11, 30), LocalDateTime.of(d1y, d1m, d1day, 14, 45), yRule));
        search.addSchedule(schedule(k013, 6L, LocalDateTime.of(d1y, d1m, d1day, 16, 0), LocalDateTime.of(d1y, d1m, d1day, 19, 15), mRule));
        search.addSchedule(schedule(k021, 7L, LocalDateTime.of(d1y, d1m, d1day, 7, 0), LocalDateTime.of(d1y, d1m, d1day, 9, 20), yRule));
        search.addSchedule(schedule(k022, 8L, LocalDateTime.of(d1y, d1m, d1day, 9, 30), LocalDateTime.of(d1y, d1m, d1day, 12, 50), yRule));
        search.addSchedule(schedule(k023, 9L, LocalDateTime.of(d1y, d1m, d1day, 13, 0), LocalDateTime.of(d1y, d1m, d1day, 16, 20), jRule));
        search.addSchedule(schedule(k051, 10L, LocalDateTime.of(d1y, d1m, d1day, 10, 0), LocalDateTime.of(d1y, d1m, d1day, 17, 30), yRule));
        search.addSchedule(schedule(k052, 11L, LocalDateTime.of(d1y, d1m, d1day, 17, 0), LocalDateTime.of(d1y, d1m, d1day, 1, 30), yRule));
        search.addSchedule(schedule(k053, 12L, LocalDateTime.of(d1y, d1m, d1day, 20, 0), LocalDateTime.of(d1y, d1m, d1day, 4, 30), fRule));
        search.addSchedule(schedule(k061, 13L, LocalDateTime.of(d1y, d1m, d1day, 9, 0), LocalDateTime.of(d1y, d1m, d1day, 16, 0), yRule));
        search.addSchedule(schedule(k062, 14L, LocalDateTime.of(d1y, d1m, d1day, 15, 30), LocalDateTime.of(d1y, d1m, d1day, 23, 30), yRule));
        search.addSchedule(schedule(k071, 15L, LocalDateTime.of(d1y, d1m, d1day, 8, 30), LocalDateTime.of(d1y, d1m, d1day, 13, 30), yRule));
        search.addSchedule(schedule(k072, 16L, LocalDateTime.of(d1y, d1m, d1day, 13, 0), LocalDateTime.of(d1y, d1m, d1day, 19, 0), yRule));
        search.addSchedule(schedule(k081, 17L, LocalDateTime.of(d1y, d1m, d1day, 9, 30), LocalDateTime.of(d1y, d1m, d1day, 13, 0), yRule));
        search.addSchedule(schedule(k082, 18L, LocalDateTime.of(d1y, d1m, d1day, 12, 30), LocalDateTime.of(d1y, d1m, d1day, 17, 0), yRule));
        search.addSchedule(schedule(k091, 19L, LocalDateTime.of(d1y, d1m, d1day, 9, 30), LocalDateTime.of(d1y, d1m, d1day, 19, 0), yRule));
        search.addSchedule(schedule(k092, 20L, LocalDateTime.of(d1y, d1m, d1day, 20, 30), LocalDateTime.of(d2y, d2m, d2day, 7, 0), yRule));
        search.addSchedule(schedule(k101, 21L, LocalDateTime.of(d1y, d1m, d1day, 10, 0), LocalDateTime.of(d1y, d1m, d1day, 18, 30), yRule));
        search.addSchedule(schedule(k102, 22L, LocalDateTime.of(d1y, d1m, d1day, 18, 0), LocalDateTime.of(d2y, d2m, d2day, 7, 0), yRule));
        search.addSchedule(schedule(k103, 23L, LocalDateTime.of(d1y, d1m, d1day, 12, 0), LocalDateTime.of(d1y, d1m, d1day, 20, 30), jRule));
        search.addSchedule(schedule(k105, 24L, LocalDateTime.of(d1y, d1m, d1day, 14, 0), LocalDateTime.of(d1y, d1m, d1day, 22, 30), fRule));
        search.addSchedule(schedule(k111, 25L, LocalDateTime.of(d1y, d1m, d1day, 16, 0), LocalDateTime.of(d2y, d2m, d2day, 5, 30), yRule));
        search.addSchedule(schedule(k121, 26L, LocalDateTime.of(d1y, d1m, d1day, 19, 0), LocalDateTime.of(d2y, d2m, d2day, 16, 0), yRule));
        search.addSchedule(schedule(k131, 27L, LocalDateTime.of(d1y, d1m, d1day, 8, 0), LocalDateTime.of(d1y, d1m, d1day, 23, 30), yRule));
        search.addSchedule(schedule(k141, 28L, LocalDateTime.of(d1y, d1m, d1day, 10, 30), LocalDateTime.of(d1y, d1m, d1day, 1, 0), yRule));
        search.addSchedule(schedule(k161, 29L, LocalDateTime.of(d1y, d1m, d1day, 11, 0), LocalDateTime.of(d1y, d1m, d1day, 3, 0), yRule));
        search.addSchedule(schedule(k171, 30L, LocalDateTime.of(d1y, d1m, d1day, 14, 0), LocalDateTime.of(d1y, d1m, d1day, 6, 30), yRule));
        search.addSchedule(schedule(k201, 31L, LocalDateTime.of(d1y, d1m, d1day, 7, 0), LocalDateTime.of(d1y, d1m, d1day, 8, 0), yRule));
        search.addSchedule(schedule(k202, 32L, LocalDateTime.of(d1y, d1m, d1day, 9, 0), LocalDateTime.of(d1y, d1m, d1day, 10, 0), yRule));

        search.addSchedule(schedule(k004, 33L, LocalDateTime.of(d2y, d2m, d2day, 10, 0), LocalDateTime.of(d2y, d2m, d2day, 13, 25), yRule));
        search.addSchedule(schedule(k014, 34L, LocalDateTime.of(d2y, d2m, d2day, 15, 0), LocalDateTime.of(d2y, d2m, d2day, 18, 15), yRule));
        search.addSchedule(schedule(k024, 35L, LocalDateTime.of(d2y, d2m, d2day, 12, 0), LocalDateTime.of(d2y, d2m, d2day, 15, 20), jRule));
        search.addSchedule(schedule(k054, 36L, LocalDateTime.of(d2y, d2m, d2day, 8, 0), LocalDateTime.of(d2y, d2m, d2day, 15, 30), yRule));
        search.addSchedule(schedule(k063, 37L, LocalDateTime.of(d2y, d2m, d2day, 10, 0), LocalDateTime.of(d2y, d2m, d2day, 17, 0), bRule));
        search.addSchedule(schedule(k073, 38L, LocalDateTime.of(d2y, d2m, d2day, 14, 0), LocalDateTime.of(d2y, d2m, d2day, 19, 0), mRule));
        search.addSchedule(schedule(k083, 39L, LocalDateTime.of(d2y, d2m, d2day, 16, 0), LocalDateTime.of(d2y, d2m, d2day, 19, 30), fRule));
        search.addSchedule(schedule(k093, 40L, LocalDateTime.of(d2y, d2m, d2day, 9, 0), LocalDateTime.of(d2y, d2m, d2day, 18, 30), yRule));
        search.addSchedule(schedule(k104, 41L, LocalDateTime.of(d2y, d2m, d2day, 19, 0), LocalDateTime.of(d3y, d3m, d3day, 8, 0), jRule));
        search.addSchedule(schedule(k112, 42L, LocalDateTime.of(d2y, d2m, d2day, 11, 0), LocalDateTime.of(d2y, d2m, d2day, 19, 30), yRule));
        search.addSchedule(schedule(k122, 43L, LocalDateTime.of(d2y, d2m, d2day, 21, 0), LocalDateTime.of(d3y, d3m, d3day, 18, 0), yRule));
        search.addSchedule(schedule(k132, 44L, LocalDateTime.of(d2y, d2m, d2day, 14, 0), LocalDateTime.of(d2y, d2m, d2day, 5, 30), yRule));
        search.addSchedule(schedule(k142, 45L, LocalDateTime.of(d2y, d2m, d2day, 16, 0), LocalDateTime.of(d2y, d2m, d2day, 6, 30), yRule));
        search.addSchedule(schedule(k162, 46L, LocalDateTime.of(d2y, d2m, d2day, 12, 0), LocalDateTime.of(d2y, d2m, d2day, 4, 0), yRule));
        search.addSchedule(schedule(k172, 47L, LocalDateTime.of(d2y, d2m, d2day, 17, 0), LocalDateTime.of(d2y, d2m, d2day, 9, 30), yRule));
        search.addSchedule(schedule(k203, 48L, LocalDateTime.of(d2y, d2m, d2day, 11, 0), LocalDateTime.of(d2y, d2m, d2day, 14, 0), yRule));
        search.addSchedule(schedule(k301, 49L, LocalDateTime.of(d2y, d2m, d2day, 18, 0), LocalDateTime.of(d3y, d3m, d3day, 7, 0), yRule));
        search.addSchedule(schedule(k401, 50L, LocalDateTime.of(d2y, d2m, d2day, 10, 0), LocalDateTime.of(d2y, d2m, d2day, 16, 0), yRule));

        search.addSchedule(schedule(k064, 51L, LocalDateTime.of(d3y, d3m, d3day, 8, 0), LocalDateTime.of(d3y, d3m, d3day, 16, 0), bRule));
        search.addSchedule(schedule(k074, 52L, LocalDateTime.of(d3y, d3m, d3day, 9, 0), LocalDateTime.of(d3y, d3m, d3day, 15, 0), mRule));
        search.addSchedule(schedule(k084, 53L, LocalDateTime.of(d3y, d3m, d3day, 11, 0), LocalDateTime.of(d3y, d3m, d3day, 14, 30), fRule));
        search.addSchedule(schedule(k094, 54L, LocalDateTime.of(d3y, d3m, d3day, 17, 0), LocalDateTime.of(d4y, d4m, d4day, 2, 30), yRule));
        search.addSchedule(schedule(k106, 55L, LocalDateTime.of(d3y, d3m, d3day, 10, 0), LocalDateTime.of(d3y, d3m, d3day, 18, 30), fRule));
        search.addSchedule(schedule(k113, 56L, LocalDateTime.of(d3y, d3m, d3day, 13, 0), LocalDateTime.of(d3y, d3m, d3day, 21, 30), bRule));
        search.addSchedule(schedule(k123, 57L, LocalDateTime.of(d3y, d3m, d3day, 15, 0), LocalDateTime.of(d4y, d4m, d4day, 12, 0), fRule));
        search.addSchedule(schedule(k133, 58L, LocalDateTime.of(d3y, d3m, d3day, 19, 0), LocalDateTime.of(d4y, d4m, d4day, 10, 30), fRule));
        search.addSchedule(schedule(k143, 59L, LocalDateTime.of(d3y, d3m, d3day, 8, 0), LocalDateTime.of(d3y, d3m, d3day, 22, 30), bRule));
        search.addSchedule(schedule(k163, 60L, LocalDateTime.of(d3y, d3m, d3day, 14, 0), LocalDateTime.of(d3y, d3m, d3day, 6, 0), fRule));
        search.addSchedule(schedule(k204, 61L, LocalDateTime.of(d3y, d3m, d3day, 14, 0), LocalDateTime.of(d3y, d3m, d3day, 17, 0), yRule));
        search.addSchedule(schedule(k302, 62L, LocalDateTime.of(d3y, d3m, d3day, 12, 0), LocalDateTime.of(d3y, d3m, d3day, 1, 0), yRule));
        search.addSchedule(schedule(k402, 63L, LocalDateTime.of(d3y, d3m, d3day, 15, 0), LocalDateTime.of(d3y, d3m, d3day, 21, 0), yRule));
        search.addSchedule(schedule(k501, 64L, LocalDateTime.of(d3y, d3m, d3day, 10, 0), LocalDateTime.of(d3y, d3m, d3day, 17, 0), yRule));

        search.addSchedule(schedule(k115, 65L, LocalDateTime.of(d4y, d4m, d4day, 10, 0), LocalDateTime.of(d4y, d4m, d4day, 19, 30), jRule));
        search.addSchedule(schedule(k116, 66L, LocalDateTime.of(d4y, d4m, d4day, 8, 0), LocalDateTime.of(d4y, d4m, d4day, 17, 30), bRule));
        search.addSchedule(schedule(k124, 67L, LocalDateTime.of(d4y, d4m, d4day, 11, 0), LocalDateTime.of(d4y, d4m, d4day, 8, 0), fRule));
        search.addSchedule(schedule(k134, 68L, LocalDateTime.of(d4y, d4m, d4day, 14, 0), LocalDateTime.of(d4y, d4m, d4day, 5, 30), yRule));
        search.addSchedule(schedule(k144, 69L, LocalDateTime.of(d4y, d4m, d4day, 16, 0), LocalDateTime.of(d4y, d4m, d4day, 6, 30), bRule));
        search.addSchedule(schedule(k164, 70L, LocalDateTime.of(d4y, d4m, d4day, 17, 0), LocalDateTime.of(d4y, d4m, d4day, 9, 0), fRule));
        search.addSchedule(schedule(k303, 71L, LocalDateTime.of(d4y, d4m, d4day, 20, 0), LocalDateTime.of(d5y, d5m, d5day, 9, 0), fRule));
        search.addSchedule(schedule(k502, 72L, LocalDateTime.of(d4y, d4m, d4day, 16, 0), LocalDateTime.of(d4y, d4m, d4day, 23, 0), yRule));

        search.addSchedule(schedule(k125, 73L, LocalDateTime.of(d5y, d5m, d5day, 9, 0), LocalDateTime.of(d5y, d5m, d5day, 6, 0), yRule));
        search.addSchedule(schedule(k135, 74L, LocalDateTime.of(d5y, d5m, d5day, 11, 0), LocalDateTime.of(d5y, d5m, d5day, 2, 30), fRule));
        search.addSchedule(schedule(k151, 75L, LocalDateTime.of(d5y, d5m, d5day, 8, 0), LocalDateTime.of(d5y, d5m, d5day, 18, 0), yRule));
        search.addSchedule(schedule(k152, 76L, LocalDateTime.of(d5y, d5m, d5day, 13, 0), LocalDateTime.of(d5y, d5m, d5day, 23, 0), yRule));
        search.addSchedule(schedule(k205, 77L, LocalDateTime.of(d5y, d5m, d5day, 18, 0), LocalDateTime.of(d5y, d5m, d5day, 19, 0), bRule));
        search.addSchedule(schedule(k206, 78L, LocalDateTime.of(d5y, d5m, d5day, 20, 0), LocalDateTime.of(d5y, d5m, d5day, 21, 0), bRule));

        search.addSchedule(schedule(k107, 79L, LocalDateTime.of(d6y, d6m, d6day, 10, 0), LocalDateTime.of(d6y, d6m, d6day, 18, 30), fRule));
        search.addSchedule(schedule(k117, 80L, LocalDateTime.of(d6y, d6m, d6day, 14, 0), LocalDateTime.of(d6y, d6m, d6day, 2, 30), jRule));
        search.addSchedule(schedule(k126, 81L, LocalDateTime.of(d6y, d6m, d6day, 18, 0), LocalDateTime.of(d7y, d7m, d7day, 15, 0), yRule));
        search.addSchedule(schedule(k136, 82L, LocalDateTime.of(d6y, d6m, d6day, 7, 0), LocalDateTime.of(d6y, d6m, d6day, 23, 30), fRule));
        search.addSchedule(schedule(k304, 83L, LocalDateTime.of(d6y, d6m, d6day, 7, 0), LocalDateTime.of(d6y, d6m, d6day, 18, 30), fRule));

        search.addSchedule(schedule(k108, 84L, LocalDateTime.of(d7y, d7m, d7day, 9, 0), LocalDateTime.of(d7y, d7m, d7day, 17, 30), yRule));
        search.addSchedule(schedule(k118, 85L, LocalDateTime.of(d7y, d7m, d7day, 11, 0), LocalDateTime.of(d7y, d7m, d7day, 19, 30), bRule));
        search.addSchedule(schedule(k127, 86L, LocalDateTime.of(d7y, d7m, d7day, 16, 0), LocalDateTime.of(d7y, d7m, d7day, 13, 0), jRule));
        search.addSchedule(schedule(k137, 87L, LocalDateTime.of(d7y, d7m, d7day, 19, 0), LocalDateTime.of(d7y, d7m, d7day, 11, 30), fRule));
        search.addSchedule(schedule(k153, 88L, LocalDateTime.of(d7y, d7m, d7day, 12, 0), LocalDateTime.of(d7y, d7m, d7day, 22, 0), yRule));

        // Iteration 3 발표 데모: 날짜별 환승/다도시 여행 코스.
        // 검색 화면의 "다도시 추천"은 ICN → NRT → JFK → LAX를 이 데이터로 연결한다.
        search.addSchedule(schedule(k001, 89L, LocalDateTime.of(d1y, d1m, d1day, 7, 10), LocalDateTime.of(d1y, d1m, d1day, 9, 35), yRule));
        search.addSchedule(schedule(k701, 90L, LocalDateTime.of(d2y, d2m, d2day, 12, 20), LocalDateTime.of(d3y, d3m, d3day, 1, 10), yRule));
        search.addSchedule(schedule(k702, 91L, LocalDateTime.of(d3y, d3m, d3day, 10, 30), LocalDateTime.of(d3y, d3m, d3day, 16, 20), yRule));
        search.addSchedule(schedule(k703, 92L, LocalDateTime.of(d4y, d4m, d4day, 11, 0), LocalDateTime.of(d5y, d5m, d5day, 2, 30), jRule));
        search.addSchedule(schedule(k704, 93L, LocalDateTime.of(d5y, d5m, d5day, 13, 10), LocalDateTime.of(d6y, d6m, d6day, 5, 50), yRule));
        search.addSchedule(schedule(k071, 94L, LocalDateTime.of(d2y, d2m, d2day, 7, 40), LocalDateTime.of(d2y, d2m, d2day, 11, 20), yRule));
        search.addSchedule(schedule(k501, 95L, LocalDateTime.of(d2y, d2m, d2day, 13, 10), LocalDateTime.of(d2y, d2m, d2day, 17, 0), yRule));
        search.addSchedule(schedule(k051, 96L, LocalDateTime.of(d3y, d3m, d3day, 8, 0), LocalDateTime.of(d3y, d3m, d3day, 15, 20), yRule));
        search.addSchedule(schedule(k301, 97L, LocalDateTime.of(d3y, d3m, d3day, 18, 30), LocalDateTime.of(d4y, d4m, d4day, 7, 30), yRule));
        search.addSchedule(schedule(k705, 98L, LocalDateTime.of(d4y, d4m, d4day, 9, 30), LocalDateTime.of(d5y, d5m, d5day, 7, 10), jRule));
        search.addSchedule(schedule(k706, 99L, LocalDateTime.of(d5y, d5m, d5day, 12, 10), LocalDateTime.of(d5y, d5m, d5day, 18, 40), yRule));

        Member me = member("김정욱", "venturers.team@gmail.com", "SKY-000-001");
        Member lee = member("이재호", "lee.jaeho@email.com", "SKY-000-002");
        Member kim = member("김경동", "kim.gyeongdong@email.com", "SKY-000-003");

        auth.registerMember(me, "SKY-000-001", "pw1234");
        auth.registerMember(lee, "SKY-000-002", "pw5678");
        auth.registerMember(kim, "SKY-000-003", "pw9012");

        List<FlightSchedule> catalog = search.getCatalog();
        FlightSchedule firstResult = catalog.isEmpty() ? null : catalog.get(0);
        return new SeedResult(me, firstResult, yRule);
    }

    public static final class SeedResult {
        public final Member member;
        public final FlightSchedule firstSchedule;
        public final FareRule defaultFareRule;
        public SeedResult(Member member, FlightSchedule firstSchedule, FareRule defaultFareRule) {
            this.member = member;
            this.firstSchedule = firstSchedule;
            this.defaultFareRule = defaultFareRule;
        }
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field f = findField(target.getClass(), fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Sample data injection failed: " + target.getClass().getSimpleName() + "." + fieldName, e);
        }
    }

    private static Field findField(Class<?> clz, String name) throws NoSuchFieldException {
        while (clz != null) {
            try {
                return clz.getDeclaredField(name);
            } catch (NoSuchFieldException ignore) {
                clz = clz.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }
}
