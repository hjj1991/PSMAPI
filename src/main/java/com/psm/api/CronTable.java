package com.psm.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.psm.api.apiserver.entity.ApiServerListEntity;
import com.psm.api.apiserver.repository.ApiServerListRepository;
import com.psm.api.workload.entity.ScheduleEntity;
import com.psm.api.workload.repository.AvailableActionRepository;
import com.psm.api.workload.repository.ScheduleRepository;
import com.psm.api.workload.repository.WorkloadRepository;
import com.psm.api.workload.service.WorkloadService;

@Component
public class CronTable {
	
	@Autowired
	WorkloadRepository workloadRepository;
	@Autowired
	AvailableActionRepository availableActionRepository;
	@Autowired
	WorkloadService workloadService;
	@Autowired
	ScheduleRepository scheduleRepository;
	
	@Autowired
	ApiServerListRepository apiServerListRepository;

    // 애플리케이션 시작 후 2분 후에 첫 실행, 그 후 매 2분마다 주기적으로 실행한다.
    @Scheduled(initialDelay = 600 * 2, fixedDelay = 60000 * 2)
    @Transactional
    public void workloadSync() {

    	System.out.println("Current Thread : {}" + Thread.currentThread().getName());
        // 실행될 로직
		List<ApiServerListEntity> apiserverList = apiServerListRepository.findAll();
//		HashMap<String, Object> result = new HashMap<String, Object>();
//		WorkloadsDto lastWorkloadsList = new WorkloadsDto();
//		List<WorkloadDto> workloadList = new ArrayList<WorkloadDto>();
		
		//액션테이블 비우기
//		availableActionRepository.deleteAllInBatch();
		availableActionRepository.resetIdAvailableActionTable();
		
		for(ApiServerListEntity apiserverInfo: apiserverList) {
			if(apiserverInfo.getCompanyIdx() == null || apiserverInfo.getCompanyIdx().getDeletedYn().equals("Y") || apiserverInfo.getDeletedYn().equals("Y")) {
				continue;
			}
			workloadService.asyncWorkload(apiserverInfo);
		}
	}
    
    @Scheduled(fixedDelay = 60000 * 1)
    @Transactional
    public void checkSchedule() throws Exception {
    	System.out.println("Current Thread : {}" + Thread.currentThread().getName());
    	List<ScheduleEntity> scheduleEntityList = scheduleRepository.findByDeletedYn("N");

    	for(ScheduleEntity scheduleEntity : scheduleEntityList) {


			workloadService.scheduleWorkloadAction(scheduleEntity);

    		
    		
    		
    	}
    }
    
}
