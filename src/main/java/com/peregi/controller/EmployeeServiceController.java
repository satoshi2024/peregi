package com.peregi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peregi.common.R;
import com.peregi.entity.Employee;
import com.peregi.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @Author:Chikai_Cho
 * @Date 2024/03/16 19:58
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeServiceController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 従業員ログイン
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1、ページから送信されたパスワード（password）をMD5ハッシュ処理する
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、ページ上で送信されたユーザー名 username に基づいてデータベースをクエリ
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3、見つからない場合は、ログイン失敗の結果が返されます
        if(emp == null){
            return R.error("ログインに失敗");
        }

        //4、パスワードの比較で矛盾がある場合、ログイン失敗の結果が返されます
        if(!emp.getPassword().equals(password)){
            return R.error("ログインに失敗");
        }

        //5、従業員のステータスを確認し、無効になっている場合は、従業員が無効であるという結果を返します
        if(emp.getStatus() == 0){
            return R.error("アカウントは無効");
        }

        //6、ログインに成功すると、従業員 ID がセッションに保存され、ログイン成功の結果が返されます
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 従業員ログアウト
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //セッションに保存されている現在ログインしている従業員の ID をクリアします
        request.getSession().removeAttribute("employee");
        return R.success("ログアウトが成功");
    }

    /**
     * 新しい従業員を追加
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新しい従業員を追加し,従業員情報：{}",employee.toString());

        //初期パスワードを「123456」に設定し、MD5ハッシュ処理を行います
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //現在ログインしているユーザーのIDを取得します
        Long empId = (Long) request.getSession().getAttribute("employee");

        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新しい従業員を追加成功した");
    }

    /**
     * 従業員情報のページネーション検索
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {},pageSize = {},name = {}" ,page,pageSize,name);

        //ページネーションコンストラクターを構築する
        Page pageInfo = new Page(page,pageSize);

        //条件コンストラクタ
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //フィルタの追加
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //並べ替え基準の追加
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //クエリを実行する
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * IDに基づいて従業員情報を変更します
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

        Long empId = (Long)request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return R.success("従業員情報の変更に成功した");
    }

    /**
     * IDに基づいて従業員情報を検索します
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("IDに基づいて従業員情報を検索し");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("対応する従業員情報が見つかりません");
    }
}
