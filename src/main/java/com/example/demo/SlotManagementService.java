package com.example.demo;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

@Service
public class SlotManagementService {
	@Autowired
	JdbcTemplate jdbc;

	private static final String APPLICATION_NAME = "CalendarTest";
	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	/** Directory to store authorization tokens for this application. */
	private static final String TOKENS_DIRECTORY_PATH = "tokens";

	/**
	 * Global instance of the scopes required by this quickstart. If modifying these
	 * scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

	private static final String SUMMARY = "IT Patch Management";
	private static final String LOCATION = "800 Howard St., San Francisco, CA 94103";
	private static final String DESCRIPTION = "1Great chance to resolve IT issues seamlessly!";
	private static final String RECURRENTCE_RULE = "RRULE:FREQ=DAILY;COUNT=1";
	private static final String calendarId = "primary";
	private static final int DAY_START_TIME = 9;
	private static final int DAY_END_TIME = 17;
	private static final String DAY_TYPE_WEEKDAY = "WEEKDAY";
	private static final String DAY_TYPE_WEEKEND = "WEEKEND";
	private static final String DAY_SLOT_START_TIME = "09:00";
	private static final String DAY_SLOT_END_TIME = "17:00";
	private static final String SCOPE_IDENTITY_QUERY = "SELECT SCOPE_IDENTITY()";

	long no_of_days = 0;
	String[] slot_start_date_time = null, slot_end_date_time = null;

	
	public String insertslot(String slot_start, String slot_end, String it_email_list, String attendee_email_list,
			String region, boolean is_booked, String time_zone) throws ParseException {

		String query1 = "insert into slots(slot_start,slot_end, time_zone, it_email_list,attendee_email_list,region,is_booked) values('2022-08-09 07:30:30','2022-08-09 08:30:30', 'EST','saurabh.gupta1@hcl.com','saurabh22045@gmail.com', 'Spain', 0)";

		String query2 = "insert into slots(slot_start,slot_end, time_zone, it_email_list,attendee_email_list,region,is_booked) values('"
				+ slot_start + "','" + slot_end + "', '" + time_zone + "','" + it_email_list + "','"
				+ attendee_email_list + "', '" + region + "', " + BooleanUtils.toInteger(is_booked) + ")";

		jdbc.execute(query2);
		return "slot created Successfully";
	}

	public String insertslots(String slot_start, String slots_end, String slot_duration, String it_email_list,
			String attendee_email_list, String region, boolean is_booked, String time_zone, boolean include_weekends,
			String holidays, String create_event) throws ParseException, IOException, GeneralSecurityException {

		getSlotDurationDays(slot_start, slots_end);
		String slot_end = "";

		String query1 = "insert into slots(slot_start,slot_end, time_zone, it_email_list,attendee_email_list,region,is_booked) values('2022-08-09 07:30:30','2022-08-09 08:30:30', 'EST','saurabh.gupta1@hcl.com','saurabh22045@gmail.com', 'Spain', 0)";

		String startDate = slot_start_date_time[0];
		String currentSlotStartDate, currentSlotStartTime;
		String currentDate;
		currentDate = startDate;
		String query2 = "";

		int totalSlotsPerDay = ((DAY_END_TIME - DAY_START_TIME) * 60) / Integer.parseInt(slot_duration);
		// int totalSlots = (slots_end - slot_start)/slot_duration;
		// String[] slot_start_date_time = null, slot_end_date_time = null;

		int i = 0;
		int j = 0;
		currentSlotStartDate = slot_start_date_time[0];
		currentSlotStartTime = slot_start_date_time[1];
		// LocalDateTime dateTime = LocalDateTime.parse(slot_start);
		String slot_start_tmp = slot_start;

		while (i <= no_of_days) {
			j = 0;
			while (j < totalSlotsPerDay) {
				if (!StringUtils.contains(holidays, currentDate)) {
					if (dayType(currentDate).equalsIgnoreCase(DAY_TYPE_WEEKDAY)
							|| (dayType(currentDate).equalsIgnoreCase(DAY_TYPE_WEEKEND) && include_weekends)) {
						LocalDateTime dateTime = LocalDateTime.parse(slot_start_tmp);
						slot_end = dateTime.plusMinutes(Long.parseLong(slot_duration)).toString();

						if (StringUtils.isBlank(attendee_email_list)) {
							attendee_email_list = it_email_list;
						}
						query2 = "insert into slots(slot_start,slot_end, time_zone, it_email_list,attendee_email_list,region,is_booked) values('"
								+ slot_start_tmp + "','" + slot_end + "', '" + time_zone + "','" + it_email_list + "','"
								+ attendee_email_list + "', '" + region + "', " + BooleanUtils.toInteger(is_booked)
								+ ")";
						// jdbc.execute(query2);

						final String SQL = query2;
						KeyHolder keyHolder = new GeneratedKeyHolder();
						jdbc.update(connection -> {
							PreparedStatement ps = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
							return ps;
						}, keyHolder);

						int requestId = keyHolder.getKey().intValue();

						System.err.print("Key--->" + requestId);
						SlotModel currentSlot = findSingleSlot(requestId);

					/**	if (Boolean.TRUE.toString().equalsIgnoreCase(create_event))
							bookSlotAndCreateEvent(currentSlot.getSlot_start(),
									currentSlot.getSlot_end(), attendee_email_list, requestId, time_zone,
									Boolean.toString(is_booked)); */
					}
				}
				j++;
				slot_start_tmp = slot_end;
			}
			LocalDate dateTime = LocalDate.parse(slot_start_date_time[0]);
			currentDate = dateTime.plusDays(i + 1).toString();
			slot_start_tmp = currentDate + "T" + DAY_SLOT_START_TIME;
			i++;
		}

		return "slots created Successfully";
	}

	private void getSlotDurationDays(String slot_start, String slots_end) throws ParseException {
		slot_start_date_time = StringUtils.split(slot_start, 'T');
		slot_end_date_time = StringUtils.split(slots_end, 'T');

		SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH);
		Date firstDate = sdf.parse(slot_start_date_time[0]);
		Date secondDate = sdf.parse(slot_end_date_time[0]);

		long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
		no_of_days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

		System.err.println(no_of_days);
	}


	private String dayType(String slot_start_date) {
		String dayType;
		LocalDate date = LocalDate.parse(slot_start_date);
		if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
			dayType = DAY_TYPE_WEEKEND;
		} else {
			dayType = DAY_TYPE_WEEKDAY;
		}
		return dayType;
	}

	public SlotModel findSingleSlot(int requestId) {

		String sql = "SELECT * FROM slots where id=" + requestId;

		List<SlotModel> slots = new ArrayList<>();

		List<Map<String, Object>> rows = jdbc.queryForList(sql);

		for (Map row : rows) {
			SlotModel obj = new SlotModel();
			obj.setId(((Long) row.get("id")));

			obj.setSlot_start(((Timestamp) row.get("slot_start")).toString());
			obj.setSlot_end(((Timestamp) row.get("slot_end")).toString());
			obj.setTime_zone(((String) row.get("time_zone")).toString());
			obj.setIt_email_list(((String) row.get("it_email_list")).toString());
			obj.setAttendee_email_list(((String) row.get("attendee_email_list")).toString());
			obj.setRegion(((String) row.get("region")).toString());

			obj.setIs_booked(((boolean) row.get("is_booked")));

			slots.add(obj);
		}

		return slots.get(0);
	}



}
