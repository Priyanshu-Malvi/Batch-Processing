package com.demo.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.ItemListenerSupport;

import com.demo.dto.EmployeeDto;
import com.demo.model.EmployeeModel;

public class ItemFailureLoggerListener extends ItemListenerSupport<EmployeeDto, EmployeeModel>{
	
	private static Logger logger = LoggerFactory.getLogger("item.error");

    public void onReadError(Exception ex) {
        logger.error("Encountered error on read", ex);
    }

    public void onWriteError(Exception ex, List<? extends Object> items) {
        logger.error("Encountered error on write", ex);
    }
	
}
