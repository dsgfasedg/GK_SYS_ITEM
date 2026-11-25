App({
  onLaunch() {
    console.log('小程序启动')
    // 检查登录状态
    this.checkLogin();
  },

  onShow() {
    console.log('小程序显示')
  },

  onHide() {
    console.log('小程序隐藏')
  },

  checkLogin() {
    const token = wx.getStorageSync('token');
    if (!token) {
      // 如果没有token，跳转到登录页
      wx.redirectTo({
        url: '/pages/login/login'
      });
    }
  },

  globalData: {
    userInfo: null,
    token: null
  }
})