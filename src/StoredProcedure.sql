CREATE PROCEDURE MakeReservationAtNextTimeslot (
    IN instructoremail VARCHAR(50),
    IN skieremail VARCHAR(50),
    IN lessonid INTEGER,
    IN location VARCHAR(50),
    IN slope_number INTEGER,
    IN totalcost DECIMAL(10,2),
    IN expgained INTEGER
)
BEGIN
    DECLARE timeslotid INTEGER;
    DECLARE reservationid INTEGER;
    DECLARE is_available BOOLEAN;
    DECLARE tmp_count INTEGER;
    DECLARE done_looping BOOLEAN default false;

    -- Cursor declaration
--     DECLARE timeslot_cursor CURSOR WITH RETURN FOR
--     SELECT timeslot_id
--     FROM Available
--     WHERE instructor_email = instructoremail
--     AND is_booked = false;

    DECLARE timeslot_cursor CURSOR FOR SELECT timeslot_id FROM Available WHERE instructor_email = instructoremail AND is_booked = false;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done_looping = true;

    SET is_available = false;

    OPEN timeslot_cursor;

    FETCH FROM timeslot_cursor INTO timeslotid;

    reservation_loop: WHILE is_available = false AND done_looping = false DO

        -- Check if instructor is available in the current slot
        SET is_available = false;
        SELECT COUNT(*)
        INTO tmp_count
        FROM Available
        WHERE instructor_email = instructoremail
        AND timeslot_id = timeslotid
        AND is_booked = false;

        SET is_available = tmp_count > 0;

        IF is_available = true THEN
            -- Check if opening exists
            IF EXISTS(
                SELECT 1
                FROM OPENING
                WHERE LOCATION_NAME = location
                AND SLOPE_NO = slope_number
                AND TIMESLOT_ID = timeslotid
                ) THEN
                -- Get a value for reservation id
                SET reservationid = (SELECT MAX(reservation_id) + 1
                                     FROM RESERVATION);
                -- Make reservation
                INSERT INTO Reservation (reservation_id, skier_email, lesson_id, location_name, slope_no, timeslot_id, total_cost, exp_gained)
                VALUES (reservationid, skieremail, lessonid, location, slope_number, timeslotid, totalcost, expgained);

                -- Update the availability of the instructor
                UPDATE Available
                SET is_booked = true
                WHERE instructor_email = instructoremail
                  AND timeslot_id = timeslotid;

                LEAVE reservation_loop;
                ELSE SET is_available = FALSE;
            end if;

        end if;

        FETCH FROM timeslot_cursor INTO timeslotid;

    end while;

    CLOSE timeslot_cursor;
END;

DROP PROCEDURE MakeReservationAtNextTimeslot;

CALL MakeReservationAtNextTimeslot('slyMcCool@profesh.com', 'john.doe@example.com', 1, 'Whistler', 1, 100.5, 2);

CALL MakeReservationAtNextTimeslot('slyMcCool@profesh.com', 'john.doe@example.com', 1, 'Big Snow', 1, 100.5, 2);
