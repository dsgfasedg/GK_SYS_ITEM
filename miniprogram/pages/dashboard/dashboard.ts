Page({
  data: {
    tasks: [],
    todayTasks: [],
    stats: {},
    loading: true
  },

  onLoad() {
    this.loadTodayTasks();
    this.loadStats();
  },

  onShow() {
    this.loadTodayTasks();
  },

  async loadTodayTasks() {
    try {
      const today = new Date().toISOString().split('T')[0];
      const response = await (await import('../api/index')).planApi.getDayView(today);
      this.setData({ 
        tasks: response.tasks,
        todayTasks: response.tasks,
        loading: false 
      });
    } catch (error) {
      this.setData({ loading: false });
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    }
  },

  async loadStats() {
    try {
      const response = await (await import('../api/index')).planApi.getStats('week');
      this.setData({ stats: response });
    } catch (error) {
      console.error('加载统计失败', error);
    }
  },

  async completeTask(e: any) {
    const { id } = e.currentTarget.dataset;
    try {
      await (await import('../api/index')).planApi.completeTask(id);
      wx.showToast({
        title: '已完成',
        icon: 'success'
      });
      this.loadTodayTasks();
      this.loadStats();
    } catch (error) {
      wx.showToast({
        title: '操作失败',
        icon: 'none'
      });
    }
  },

  navigateToPlan() {
    wx.switchTab({
      url: '/pages/plan/plan'
    });
  }
});