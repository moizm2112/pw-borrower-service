package com.paywallet.userservice.user.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import com.paywallet.userservice.user.dto.EventDTO;
import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.enums.ProgressLevel;
import com.paywallet.userservice.user.exception.RequestAPIDetailsException;
import com.paywallet.userservice.user.model.CreateCustomerRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.sentry.Sentry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;

import com.paywallet.userservice.user.entities.HolidaysList;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.repository.ListOfHolidaysRepo;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RefreshScope
public class CommonUtil {
	
	private final String SHA256 = "SHA-256";
	
	@Value("${Luthersales_days_for_FirstDateOfPayment}")
	private String luthersales_days_for_FirstDateOfPayment;

	@Value("${decimal.part.size:2}")
	private String decimalLength;

	@Value("${time.zone:America/New_York}")
	private String timeZone;

	@Value("${date.time.pattern:MM/dd/yyyy HH:mm:ss 'EST'}")
	private String dateTimePattern;

	@Autowired
	ListOfHolidaysRepo listOfHolidaysRepo;
	
	public boolean checkIfFirstDateOfPaymentValid(String date,String clientName) throws GeneralCustomException {
		boolean isDateValid = false;

		try {
			if (StringUtils.isNotBlank(date) && StringUtils.isNotBlank(clientName)) {
				LocalDate dateFormatted = LocalDate.parse(date,
						DateTimeFormatter.ofPattern("uuuu-M-d").withResolverStyle(ResolverStyle.STRICT));
				LocalDate validFirstFirstDateOfPayment = findValidFirstFirstDateOfPayment(clientName);
				while(isHoliday(validFirstFirstDateOfPayment , listOfHolidaysRepo.findAll())) {
					validFirstFirstDateOfPayment = validFirstFirstDateOfPayment.plusDays(1);
				}
				if (dateFormatted.compareTo(validFirstFirstDateOfPayment) > 0) {
					log.info("Date " + dateFormatted + " occurs after Date " + validFirstFirstDateOfPayment);
					isDateValid = true;
				} else if (dateFormatted.compareTo(validFirstFirstDateOfPayment) < 0) {
					log.info("Date " + dateFormatted + " occurs before Date " + validFirstFirstDateOfPayment);
					isDateValid = false;
					throw new GeneralCustomException("ERROR",
							"First date of payment date cannot be less than "+validFirstFirstDateOfPayment);
				} else if (dateFormatted.compareTo(validFirstFirstDateOfPayment) == 0) {
					log.info("Both dates are equal");
					isDateValid = true;
				}
				while(isHoliday(dateFormatted , listOfHolidaysRepo.findAll())) {
					isDateValid = false;
					throw new GeneralCustomException("ERROR",
							"First date of payment date cannot be either on Saturday or on Sunday or on a Holiday");
				}
			}

		} catch (DateTimeParseException e) {
			Sentry.captureException(e);
			isDateValid = false;
			log.error("Please enter date in YYYY-MM-DD format : {}",e.getMessage());
			throw new GeneralCustomException("ERROR",e.getMessage() + " Please enter correct date in YYYY-MM-DD format");

		}
		log.info("isDateValid:: " + isDateValid);
		
		return isDateValid;
	}

	public double convertToDouble(String strValue) {
		double convertedVal = 0;
		try {
			if (StringUtils.isNotBlank(strValue)) {
				convertedVal = Double.parseDouble(strValue);
			}
		} catch (Exception ex) {
			Sentry.captureException(ex);
			log.error(" Error while parsing the amount : {} ", ex.getMessage(),ex);
		}
		return convertedVal;
	}

	public String hashString(String originalString) {
		if(StringUtils.isBlank(originalString))
			return "";

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(SHA256);
		} catch (NoSuchAlgorithmException e) {
			Sentry.captureException(e);
			log.error("Error while generating hash : {}",e.getMessage(),e);
		}
		byte[] hashBytes = md.digest(originalString.getBytes(StandardCharsets.UTF_8));
		String hashedString = Base64.getEncoder().encodeToString(hashBytes);
		return hashedString;

	}

	public LocalDate findValidFirstFirstDateOfPayment(String clientName){
		long plusDays=0;
		switch(clientName) {
			case "LutherSales":
				plusDays= Long.parseLong(luthersales_days_for_FirstDateOfPayment);
				break;
			case "MoneyTree":
				plusDays= Long.parseLong(luthersales_days_for_FirstDateOfPayment);
				break;	
		}

		LocalDate dateWithPlusDays = LocalDate.now().plusDays(plusDays);
		log.info("Date "+LocalDate.now()+" plus "+plusDays+" days is "+dateWithPlusDays);

		return dateWithPlusDays;
	}


	public boolean isHoliday(LocalDate localDate, List<HolidaysList> holidaysList) {

		if((DayOfWeek.SATURDAY == DayOfWeek.from(localDate)) || (DayOfWeek.SUNDAY == DayOfWeek.from(localDate)) || checkHolidayDate(localDate.toString(), holidaysList)) {
			return (true);
		} else {
			return false;
		}
	}

	public boolean checkHolidayDate(String date, List<HolidaysList> holidaysList) {
		boolean result = false;
		for (HolidaysList str : holidaysList) {
			if (str.getHoliday().contains(date)) {
				result = true;
				break;
			} else {
				result = false;
			}
		}
		return result;
	}

	public String getFormattedAmount(int amount){
		try {
			return String.format("%."+decimalLength+"f",Double.valueOf(amount));
		}catch (Exception e){
			log.error(" Exception while formatting the amount : {} ",e.getMessage());
		}
		return  String.valueOf(amount);
	}

	public String getFormattedAmount(double amount){
		try {
			return String.format("%."+decimalLength+"f",amount);
		}catch (Exception e){
			log.error(" Exception while formatting the amount : {} ",e.getMessage());
		}
		return  String.valueOf(amount);
	}

	public EventDTO prepareEvent(String requestId, String code, String source, String message, ProgressLevel level){
		return EventDTO.builder()
				.requestId(requestId)
				.code(code)
				.source(source)
				.message(message)
				.level(level)
				.dateTime(getESTTime())
				.build();
	}

	public String getESTTime() {
		try {
			DateTimeFormatter etFormat = DateTimeFormatter.ofPattern(dateTimePattern);
			ZonedDateTime currentTime = ZonedDateTime.now();
			ZonedDateTime currentETime = currentTime
					.withZoneSameInstant(ZoneId.of(timeZone));
			return etFormat.format(currentETime);
		} catch (Exception e) {
			log.error("Error while parsing date : {}",e);
		}
		return null;
	}

	public void addSentryTransactionIdentifier(final String requestId) {
		Sentry.configureScope(scope -> {
			scope.setTag("transaction_id", requestId);
		});
	}

	public String decodeRequestId(String jwtToken) {
		Optional.ofNullable(jwtToken).orElseThrow(()->new RuntimeException("Empty jwt token"));
		jwtToken = jwtToken.trim();
		int i = jwtToken.lastIndexOf('.');
		String withoutSignature = jwtToken.substring(0, i+1);
		Jwt<Header, Claims> untrusted = Jwts.parser().parseClaimsJwt(withoutSignature);
		return untrusted.getBody().getSubject();
	}

}
