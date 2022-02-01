package com.larkmidtable.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.larkmidtable.admin.entity.APIConfig;
import com.larkmidtable.admin.entity.JobDatasource;
import com.larkmidtable.admin.entity.ResponseData;
import com.larkmidtable.admin.mapper.APIConfigMapper;
import com.larkmidtable.admin.service.APIConfigService;
import com.larkmidtable.admin.service.JobDatasourceService;
import com.larkmidtable.admin.util.DruidDataSource;
import com.larkmidtable.core.biz.model.ReturnT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 *
 * @Author:
 * @Date:
 * @Description:
 **/
@RestController
@RequestMapping("/api/apiConfig")
@Api(tags = "OpenAPI的操作")
public class APIConfigController extends BaseController {

    @Autowired
    private APIConfigService apiConfigService;

	@Autowired
	private JobDatasourceService jobJdbcDatasourceService;

	@Resource
	private APIConfigMapper apiConfigMapper;

    /**
     * Get all project
     *
     * @return
     */
	@ApiOperation("获取所有数据")
	@GetMapping("/list")
	public ReturnT<List<APIConfig>> selectList() {
		// page list
		List<APIConfig> list = apiConfigMapper.findAll();
		return new ReturnT<> (list);
	}
    /**
     * 新增数据
     *
     * @param entity 实体对象
     * @return 新增结果
     */
    @ApiOperation("新增数据")
	@PostMapping("/add")
    public ReturnT<String> insert(HttpServletRequest request, @RequestBody APIConfig entity) {
		entity.setName(entity.getName());
		entity.setParams(entity.getParams());
		entity.setCreate_time(new Date().toString());
		entity.setDatasource_id(entity.getDatasource_id());
		entity.setDescribe(entity.getDescribe());
		entity.setPath(entity.getPath());
		entity.setGroup_id(entity.getGroup_id());
		entity.setSql_text(entity.getSql_text());
        this.apiConfigMapper.save(entity);
		return ReturnT.SUCCESS;
    }

    /**
     * 修改数据
     *
     * @param entity 实体对象
     * @return 修改结果
     */
    @ApiOperation("修改数据")
	@PostMapping(value = "/update")
	public ReturnT<String> update(@RequestBody APIConfig entity) {
		APIConfig project = apiConfigMapper.getById(entity.getId());
        project.setName(entity.getName());
        project.setParams(entity.getParams());
        project.setUpdate_time(new Date().toString());
        project.setDatasource_id(entity.getDatasource_id());
        project.setDescribe(entity.getDescribe());
        project.setPath(entity.getPath());
        project.setGroup_id(entity.getGroup_id());
        project.setSql_text(entity.getSql_text());
		apiConfigMapper.update(project);
		return ReturnT.SUCCESS;
    }

    /**
     * 删除数据
     *
     * @param id 主键结合
     * @return 删除结果
     */
	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	@ApiOperation("删除数据")
	public ReturnT<String> delete(int id) {
		int result = apiConfigMapper.delete(id);
		return result != 1 ? ReturnT.FAIL : ReturnT.SUCCESS;
	}

	@PostMapping(value ="/execute")
	@ApiOperation("执行API的查询")
	public ResponseData executeSql(@RequestBody APIConfig apiConfig) {
		try {
			String params = apiConfig.getParams();
			String datasourceId = apiConfig.getDatasource_id();
			String sql_text = apiConfig.getSql_text();
			Map<String, Object> paramsMap = JSON.parseObject(params, LinkedHashMap.class);
			// 获取DateSource
			JobDatasource datasource = this.jobJdbcDatasourceService.getById(datasourceId);
			// 执行结果
			Object result = DruidDataSource.executeSql(datasource,sql_text,paramsMap);
			return ResponseData.successWithData(result);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseData.fail(e.getMessage());
		}
	}
}
