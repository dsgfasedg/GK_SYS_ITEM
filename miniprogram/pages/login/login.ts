Page({
  data: {
    phone: '',
    code: '',
    countdown: 0,
    timer: null
  },

  onLoad() {
    // 检查是否已登录
    const token = wx.getStorageSync('token');
    if (token) {
      wx.switchTab({
        url: '/pages/dashboard/dashboard'
      });
    }
  },

  onPhoneInput(e: any) {
    this.setData({ phone: e.detail.value });
  },

  onCodeInput(e: any) {
    this.setData({ code: e.detail.value });
  },

  sendCode() {
    const { phone } = this.data;
    if (!phone || !/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({
        title: '手机号格式错误',
        icon: 'none'
      });
      return;
    }

    // 发送验证码（实际需要调用后端接口）
    wx.showToast({
      title: '验证码已发送',
      icon: 'success'
    });

    // 开始倒计时
    this.setData({ countdown: 60 });
    const timer = setInterval(() => {
      if (this.data.countdown <= 0) {
        clearInterval(timer);
        return;
      }
      this.setData({ countdown: this.data.countdown - 1 });
    }, 1000);
    
    this.setData({ timer });
  },

  login() {
    const { phone, code } = this.data;
    
    if (!phone) {
      wx.showToast({
        title: '请输入手机号',
        icon: 'none'
      });
      return;
    }
    
    if (!code) {
      wx.showToast({
        title: '请输入验证码',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({ title: '登录中...' });

    // 实际调用登录接口
    setTimeout(() => {
      wx.hideLoading();
      wx.showToast({
        title: '登录成功',
        icon: 'success'
      });
      
      // 保存token
      wx.setStorageSync('token', 'demo-token');
      
      wx.switchTab({
        url: '/pages/dashboard/dashboard'
      });
    }, 1000);
  },

  onUnload() {
    if (this.data.timer) {
      clearInterval(this.data.timer);
    }
  }
});