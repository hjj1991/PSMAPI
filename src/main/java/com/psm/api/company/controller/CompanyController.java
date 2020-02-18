package com.psm.api.company.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.psm.api.common.response.SingleResult;
import com.psm.api.common.response.service.ResponseService;
import com.psm.api.company.dto.FindCompanyDto;
import com.psm.api.company.dto.InsertCompanyDto;
import com.psm.api.company.dto.UpdateCompanyDto;
import com.psm.api.company.entity.CompanyEntity;
import com.psm.api.company.repository.CompanyRepository;
import com.psm.api.company.service.CompanyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = { "3. Company" })
@RequiredArgsConstructor
@RestController // 결과값을 JSON으로 출력합니다.
@RequestMapping(value = "/v1/company")
public class CompanyController {

	@Autowired
	CompanyRepository companyRepository;
	@Autowired
	ResponseService responseService;
	@Autowired
	CompanyService companyService;

	@ApiImplicitParams({
			@ApiImplicitParam(name = "X_AUTH_TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header") })
	@ApiOperation(value = "소속회사 조회", notes = "소속회사를 조회한다")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public SingleResult<?> findCompany(FindCompanyDto findCompanyDto) {
//		responseService.getSingleResult(companyRepository.findByDeletedYn("Y", pageable));
		Page<CompanyEntity> result = companyService.findCompany(findCompanyDto);

		return responseService.getSingleResult(result);

//		return responseService.getSingleResult(userService.getUserDetail(userRepository
//				.findByUserId(jwtTokenProvider.getUserPk(authToken)).orElseThrow(CUserNotFoundException::new)));
	}

	@ApiImplicitParams({
			@ApiImplicitParam(name = "X_AUTH_TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header") })
	@ApiOperation(value = "소속회사 조회", notes = "소속회사를 조회한다")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public SingleResult<?> insertCompany(@RequestBody List<InsertCompanyDto> insertCompanyList) {
		HashMap<String, Object> result = companyService.insertCompany(insertCompanyList);

//		return responseService.getSingleResult(result);
		return responseService.getSingleResult(result);

	}

	@ApiImplicitParams({
			@ApiImplicitParam(name = "X_AUTH_TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header") })
	@ApiOperation(value = "소속회사 수정", notes = "소속회사를 수정한다.")
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public SingleResult<?> updateCompany(@RequestBody UpdateCompanyDto updateCompanyValue) throws Exception {
		CompanyEntity result = companyService.updateCompany(updateCompanyValue);

//	return responseService.getSingleResult(result);
		return responseService.getSingleResult(result);

	}
}
