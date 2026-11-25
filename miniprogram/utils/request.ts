// 全局配置
const config = {
  baseUrl: 'http://localhost:8080/api/v1'
}

// 请求拦截器
function request(url: string, method: string = 'GET', data?: any) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token');
    
    wx.request({
      url: `${config.baseUrl}${url}`,
      method: method,
      data: data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
      },
      success: (res: any) => {
        if (res.statusCode === 200) {
          resolve(res.data);
        } else if (res.statusCode === 401) {
          wx.removeStorageSync('token');
          wx.redirectTo({
            url: '/pages/login/login'
          });
          reject(res);
        } else {
          wx.showToast({
            title: res.data.message || '请求失败',
            icon: 'none'
          });
          reject(res);
        }
      },
      fail: (err) => {
        wx.showToast({
          title: '网络连接失败',
          icon: 'none'
        });
        reject(err);
      }
    });
  });
}

export default {
  get: (url: string, data?: any) => request(url, 'GET', data),
  post: (url: string, data?: any) => request(url, 'POST', data),
  put: (url: string, data?: any) => request(url, 'PUT', data),
  delete: (url: string, data?: any) => request(url, 'DELETE', data),
}