
function isValidUsername (str) {
  return ['admin', 'editor'].indexOf(str.trim()) >= 0;
}

function isExternal (path) {
  return /^(https?:|mailto:|tel:)/.test(path);
}

function isCellPhone (val) {

    return false

}

//校验账号
function checkUserName (rule, value, callback){
  if (value == "") {
    callback(new Error("アカウントを入力してください"))
  } else if (value.length > 20 || value.length <3) {
    callback(new Error("アカウントの長さは3から20文字である必要"))
  } else {
    callback()
  }
}

//校验姓名
function checkName (rule, value, callback){
  if (value == "") {
    callback(new Error("名前を入力してください"))
  } else if (value.length > 32) {
    callback(new Error("アカウントの長さは1から12文字である必要"))
  } else {
    callback()
  }
}

function checkPhone (rule, value, callback){
  // let phoneReg = /(^1[3|4|5|6|7|8|9]\d{9}$)|(^09\d{8}$)/;
  if (value == "") {
    callback(new Error("携帯番号を入力してください"))
  } else if (!isCellPhone(value)) {//引入methods中封装的检查手机格式的方法
    callback(new Error("正しい携帯番号を入力してください"))
  } else {
    callback()
  }
}


function validID (rule,value,callback) {
  // 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
  let reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  if(value == '') {
    callback(new Error('请输入身份证号码'))
  } else if (reg.test(value)) {
    callback()
  } else {
    callback(new Error('身份证号码不正确'))
  }
}