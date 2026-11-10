// components/timetable.js
Component({
properties: {
  timetables: {
    value: []
},
timetableType: {
  value: []
},
jiaoshi: {
  value: []
},
ids: {
  value: []
},
  },
  data: {  
    currentWeekIndex: null, // 用于存储当前星期的索引  
    week:['一', '二', '三', '四', '五', '六', '日'],
    // timetables:[],
    allPalette: [ '#f05261', '#48a8e4', '#ffd061', '#52db9a', '#70d3e6', '#52db9a', '#3f51b5', '#f3d147', '#4adbc3', '#673ab7', '#f3db49', '#76bfcd', '#b495e1', '#ff9800', '#8bc34a'],
    colorArrays: [  
      '#70eb55', '#ffc450', '#ff9bd7', '#86affe', '#58eea4', '#ffa17d', '#1cbbb4', '#6964ad',  
      '#d42245', '#218285'  
    ],  
    timeColor: '#000000',  
    courseList: [],  
    width: 0,  
    height: 0,  
    statusBarHeight: 0,  
    showWeeks: false,  
    nowWeek: 1  ,
    mergeList:[]
  }, 
  
  /**
   * 组件的初始数据
   */

  ready() {
    const systemInfo = wx.getSystemInfoSync();  
    this.setData({  
      width: systemInfo.windowWidth,  
      height: systemInfo.windowHeight,  
      statusBarHeight: systemInfo.statusBarHeight,  
      currentWeekIndex: this.todayWeekIndex() 
    });  
    console.log('sakjsiajsas',this.data.timetables);
    this.courseData();
    this.todayWeekIndex()
  },  
  
  onShow(){
    console.log('sakjsiajsas',this.data.timetables);
    this.courseData();
    this.todayWeekIndex()
  },
  /**
   * 组件的方法列表
   */
  methods: {
    courseData () {
      // 为数据标记背景颜色的函数
      let paletteIndex = 0
      const getBackgroundColor = () => {
        const backgroundColor = this.data.allPalette[paletteIndex]
        paletteIndex++
        if (paletteIndex >= this.data.allPalette.length) {
          paletteIndex = 0
        }
        return backgroundColor
      }
  
      // 合并
      const listMerge = []
      var that=this.data;
      console.log('that.jiaoshi',that.jiaoshi);
     this.data.timetables.forEach(function (list, i) {
        if (!listMerge[i]) {
          listMerge[i] = []
        }
        list.forEach(function (item, index) {
          if (!index) {
            return listMerge[i].push({
               name: item, length: 1, backgroundColor: item === '' ? 'none' : getBackgroundColor(),
            jiaoshi: that.jiaoshi[i][index],
            id: that.ids[i][index]
          })
          }
          if (item === (listMerge[i][index - 1] || {}).name && item) {
            const sameIndex = (listMerge[i][index - 1] || {}).sameIndex
            if (sameIndex || sameIndex === 0) {
              listMerge[i][sameIndex].length++
              return listMerge[i].push({ name: item, length: 0, sameIndex: sameIndex })
            }
            listMerge[i][index - 1].length++
            return listMerge[i].push({ name: item, length: 0, sameIndex: index - 1 })
          } else {
            return listMerge[i].push({ name: item, length: 1, backgroundColor: item === '' ? 'none' : getBackgroundColor(),    jiaoshi: that.jiaoshi[i][index],
            id: that.ids[i][index] })
          }
        })
      })
      this.setData({
        mergeList:listMerge
      })
      console.log('listMerge',listMerge);
      return listMerge
    },
    todayWeekIndex () {
      let weekIndex = new Date().getDay() - 1
      if (weekIndex === -1) {
        weekIndex = 6
      }
      return weekIndex
    },
    handleCourseClick (e) {
  const course= e.currentTarget.dataset.course;
  const weekIndex= e.currentTarget.dataset.weekIndex;
    const courseIndex= e.currentTarget.dataset.courseIndex;
      const data = {
        index: courseIndex + 1,
        length: course.length,
        week: this.data.week[weekIndex],
        weekIndex: weekIndex,
        name: course.name
      }
      console.log(`星期${data.week}; 第${data.index}节课; 课程名:${data.name}; 课节:${data.length}`)
      console.log(data)

      // 触发自定义事件，并传递数据  
      this.triggerEvent('courseClick', data)
    }
  }
})