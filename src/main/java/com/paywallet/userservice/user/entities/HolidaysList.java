package com.paywallet.userservice.user.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document(collection="holidaysList")
@AllArgsConstructor
@NoArgsConstructor
public class HolidaysList {
    private List<String> holiday;
}



