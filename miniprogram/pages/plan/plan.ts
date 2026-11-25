Page({
  data: {
    currentView: 'day',
    selectedDate: new Date().toISOString().split('T')[0],
    tasks: [],
    loading: true
  },

  onLoad() {
    this.loadTasks();
  },

  onDateChange(e: any) {
    this.setData({ selectedDate: e.detail.value });
    this.loadTasks();
  },

  onTabChange(e: any) {
    this.setData({ currentView: e.detail.key });
    this.loadTasks();
  },

  async loadTasks() {
    try {
      let response;
      if (this.data.currentView === 'day') {
        response = await (await import('../api/index')).planApi.getDayView(this.data.selectedDate);
      } else {
        response = await (await import('../api/index')).planApi.getWeekView();
      }
      
      this.setData({ 
        tasks: this.data.currentView === 'day' ? response.tasks : response.days,
        loading: false 
      });
    } catch (error) {
      this.setData({ loading: false });
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    }
  }
});