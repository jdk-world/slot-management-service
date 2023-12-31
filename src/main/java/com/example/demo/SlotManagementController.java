package com.example.demo;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/slot")
public class SlotManagementController {

    // Inject a service or repository that handles data access
    private final SlotManagementService slotManagementService;

    @Autowired
    public SlotManagementController(SlotManagementService slotManagementService) {
        this.slotManagementService = slotManagementService;
    }	
    
	@RequestMapping(value = "/insertslot", method = RequestMethod.POST)
	@ResponseBody
	public String insertslot(@RequestBody SlotModel slotModel)
			throws ParseException, IOException, GeneralSecurityException {

		String tmp_region = slotModel.getRegion();
		String tmp_zone = slotModel.getTime_zone();

		if (StringUtils.isNotEmpty(tmp_region) && StringUtils.equalsAnyIgnoreCase(tmp_region, "ALL")) {
			tmp_region = "Asia";
			tmp_zone = tmp_region+"/Kolkata";
		}

		String report = slotManagementService.insertslot(slotModel.getSlot_start(), slotModel.getSlot_end(),
				slotModel.getIt_email_list(), slotModel.getAttendee_email_list(), tmp_region, slotModel.isIs_booked(),
				tmp_zone);
		return report;

	}
	
	@RequestMapping(value = "/insertslots", method = RequestMethod.POST)
	@ResponseBody
	public String insertslots(@RequestBody SlotModel slotModel)
				throws ParseException, IOException, GeneralSecurityException {
		
		String tmp_region = slotModel.getRegion();
		String tmp_zone = slotModel.getTime_zone();

		if (StringUtils.isNotEmpty(tmp_region) && StringUtils.equalsAnyIgnoreCase(tmp_region, "ALL")) {
			tmp_region = "Asia";
			tmp_zone = tmp_region+"/Kolkata";
		}

		
		String report = slotManagementService.insertslots(slotModel.getSlot_start(), slotModel.getSlots_end(),
				slotModel.getSlot_duration(), slotModel.getIt_email_list(), slotModel.getAttendee_email_list(),
				tmp_region, slotModel.isIs_booked(), tmp_zone,
				slotModel.isInclude_weekends(), slotModel.getHolidays(), slotModel.getCreate_event());
		return report;

	}
	
	@RequestMapping(value = "/deleteslots", method = RequestMethod.POST)
	@ResponseBody
	public String deleteSlots(@RequestBody List<String> slotIdList)
				throws ParseException, IOException, GeneralSecurityException {
		
		String report = slotManagementService.removeSlots(slotIdList);
		return report;

	}
	
}
