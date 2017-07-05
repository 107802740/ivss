package com.zmap.login.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.base.BaseActivity;
import com.base.listener.onRuturnDataListener;
import com.base.task.UpdataPswTask;
import com.base.util.AndroidUtil;
import com.zmap.datagather.activity1.R;

public class UpdatePwdActivity extends BaseActivity {
	public TextView tvMsg = null;
	public EditText txtCfgPwd = null;
	public EditText txtLoginId = null;
	public EditText txtNewPwd = null;
	public EditText txtOldPwd = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_pwd);
		initViews();
	}

	private void initViews() {
		this.txtLoginId = ((EditText) findViewById(R.id.txtLoginId));
		this.txtOldPwd = ((EditText) findViewById(R.id.txtOldPwd));
		this.txtNewPwd = ((EditText) findViewById(R.id.txtNewPwd));
		this.txtCfgPwd = ((EditText) findViewById(R.id.txtCfgPwd));
		this.tvMsg = ((TextView) findViewById(R.id.tvMsg));
	}

	public void doCancel(View paramView) {
		finish();
	}

	public void doOK(View paramView) {
		String loginId = this.txtLoginId.getText().toString();
		String password = this.txtOldPwd.getText().toString();
		String newPassword = this.txtNewPwd.getText().toString();
		String CfgPwd = this.txtCfgPwd.getText().toString();
		if (loginId.equals("")) {
			showMsg("用户名不能为空");
			this.txtLoginId.setFocusable(true);
			this.txtLoginId.setFocusableInTouchMode(true);
			this.txtLoginId.requestFocus();
			return;
		}
		if (password.equals("")) {
			showMsg("密码不能为空");
			this.txtOldPwd.setFocusable(true);
			this.txtOldPwd.setFocusableInTouchMode(true);
			this.txtOldPwd.requestFocus();
			return;
		}
		if (newPassword.equals("")) {
			showMsg("新密码不能为空");
			this.txtNewPwd.setFocusable(true);
			this.txtNewPwd.setFocusableInTouchMode(true);
			this.txtNewPwd.requestFocus();
			return;
		}
		if (newPassword.length() < 6) {
			showMsg("密码长度不能小于6个字");
			this.txtNewPwd.setFocusable(true);
			this.txtNewPwd.setFocusableInTouchMode(true);
			this.txtNewPwd.requestFocus();
			return;
		}
		if (!AndroidUtil.judgePasswordFormat(newPassword)) {
			showMsg("密码不能包含中文,空格,美元符号,横杠和单双引号！");
			this.txtNewPwd.setFocusable(true);
			this.txtNewPwd.setFocusableInTouchMode(true);
			this.txtNewPwd.requestFocus();
			return;
		}

		if (!newPassword.equals(CfgPwd)) {
			showMsg("确认密码和新密码不一致");
			this.txtCfgPwd.setFocusable(true);
			this.txtCfgPwd.setFocusableInTouchMode(true);
			this.txtCfgPwd.requestFocus();
			return;
		}
		new UpdataPswTask(UpdatePwdActivity.this, loginId, password,
				newPassword, new onRuturnDataListener<Boolean>() {

					@Override
					public void onResult(Boolean result) {
						UpdatePwdActivity.this.finish();
					}
				}).execute();
	}
}
