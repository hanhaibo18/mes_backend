<template>
  <el-scrollbar :style="{ height: $store.state.common.compAutoHeight + 'px' }">
    <div class="comp_padding">
      <el-form
        :inline="true"
        :model="dataForm"
        @keyup.enter.native="getCompleteList()"
        size="mini"
      >
        
        <el-form-item>
          <el-form-item>
            <el-date-picker v-model="startTime" style="width: 120px"  
                            type="date"  value-format="yyyy-MM-dd"
                            placeholder="开始时间">
            </el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-date-picker v-model="endTime" style="width: 120px"
                            type="date"  value-format="yyyy-MM-dd"
                            placeholder="结束时间">
            </el-date-picker>
          </el-form-item>
		   <el-form-item>
                <el-input placeholder="工作号"  v-model="workNo" clearable
                          style="width: 180px"></el-input>
              </el-form-item>
			   <el-form-item>
                <el-input placeholder="图号"  v-model="routerNo" clearable
                          style="width: 180px"></el-input>
              </el-form-item>
			  <el-form-item>
                <el-input placeholder="跟单号"  v-model="trackNo" clearable
                          style="width: 180px"></el-input>
              </el-form-item>
			   <el-form-item>
                <el-input placeholder="员工号或名称"  v-model="userId" clearable
                          style="width: 180px"></el-input>
              </el-form-item>
        </el-form-item>
                  <el-button @click="getCompleteList()" type="primary" icon="el-icon-search">查询</el-button>
                  <el-button @click="exportexcel()" type="primary" icon="el-icon-download">导出EXCEL</el-button>
      </el-form>

      <el-row size="mini">
        <el-form :model="dataForm"
                 :inline="true"
                 size="mini">
        </el-form>
      </el-row>
      <el-table
        :data="trackList"
        border
        v-loading="trackListLoading"
        @selection-change="selection-change"
        size="mini">



        <el-table-column sortable
                         type="selection"
                         header-align="center"
                         align="center"
        >
        </el-table-column>

        <el-table-column prop="workNo"
                         header-align="center"
                         width="80"
                         align="center"
                         label="工作号">
          <template slot-scope="scope">{{scope.row.workNo}}</template>
        </el-table-column>

        <el-table-column prop="trackNo2"
                         header-align="center"
                         flex
                         align="center"
                         label="跟单编号">
          <template slot-scope="scope">{{scope.row.trackNo2}}</template>
        </el-table-column>

        <el-table-column prop="productNo"
                         header-align="center"
                         width="100"
                         align="center"
                         label="产品名称">
          <template slot-scope="scope">{{scope.row.productNo }}</template>
        </el-table-column>

      <el-table-column prop="optSequence"
                         header-align="center"
                         width="70"
                         align="center"
                         label="工序号">
          <template slot-scope="scope">{{scope.row.optSequence }}</template>
        </el-table-column>

 <el-table-column prop="optName"
                         header-align="center"
                         width="70"
                         align="center"
                         label="工序名称">
          <template slot-scope="scope">{{scope.row.optName }}</template>
        </el-table-column>

 <el-table-column prop="userId"
                         header-align="center"
                         width="70"
                         align="center"
                         label="员工号">
          <template slot-scope="scope">{{scope.row.userId }}</template>
        </el-table-column>
 <el-table-column prop="emplName"
                         header-align="center"
                         width="70"
                         align="center"
                         label="员工名">
          <template slot-scope="scope">{{scope.row.emplName }}</template>
        </el-table-column>
 <el-table-column prop="actualHours"
                         header-align="center"
                         width="90"
                         align="center"
                         label="原始工时">
          <template slot-scope="scope">{{scope.row.actualHours }}</template>
        </el-table-column>
 <el-table-column prop="reportHours"
                         header-align="center"
                         width="90"
                         align="center"
                         label="调整工时">
          <template slot-scope="scope">
		  
		   <el-input v-if="scope.row.status" v-model="scope.row.reportHours" placeholder="reportHours"></el-input>
              <span v-else>{{scope.row.reportHours}}</span>
		  </template>
        </el-table-column>
 <el-table-column prop="prepareEndHours"
                         header-align="center"
                         width="90"
                         align="center"
                         label="准结工时">
          <template slot-scope="scope">
		  
		   <el-input v-if="scope.row.status" v-model="scope.row.prepareEndHours" placeholder="prepareEndHours"></el-input>
              <span v-else>{{scope.row.prepareEndHours}}</span>
		  </template>
        </el-table-column>

<el-table-column prop="singlePieceHours"
                         header-align="center"
                         width="100"
                         align="center"
                         label="单件额定工时">
          <template slot-scope="scope">
		   <el-input v-if="scope.row.status" v-model="scope.row.singlePieceHours" placeholder="singlePieceHours"></el-input>
              <span v-else>{{scope.row.singlePieceHours}}</span>
		 </template>
        </el-table-column>

 <el-table-column prop="completeTime"
                         header-align="center"
                         width="100"
                         align="center"
                         label="报工时间">
          <template slot-scope="scope"> <span>{{ scope.row.completeTime | FormatDate('yyyy-MM-dd HH:mm:ss') }}</span></template>
        </el-table-column>
  <el-table-column
            prop="opt"
            header-align="center"
            align="center"
            width="120"
            label="操作">
            <template slot-scope="scope">
              <el-button type="text" style="padding: 5px" @click="editrow(scope.row,scope.$index)">
                {{scope.row.status?'保存':"修改"}}
              </el-button>
              <el-button v-if="scope.row.status" plain type="text" style="padding: 1px"
                         @click="scope.row.status=false">
                取消
              </el-button>
             
            </template>
          </el-table-column>
</el-table>
      <el-pagination @size-change="sizeChangeHandle"
                     @current-change="currentChangeHandle"
                     :current-page="pageIndex"
                     :page-sizes="[10, 20, 50, 100]"
                     :page-size="pageSize"
                     :total="totalPage"
                     size="mini"
                     style="margin-top:0px;float:left;width: 100%"
                     layout="total, prev, pager, next, jumper, sizes">
      </el-pagination>
    </div>
  </el-scrollbar>
</template>
<script>

import sjtj_service from '@/api/sjtj_service.js'
import {getStore} from '@/utils/storage'
import { formatDate } from '@/utils/time'
import completeService from '@/api/track_complete_service.js'
// import AddOrUpdate from './trackhead_template-add-or-update'

export default {
  data() {
    return {
      dataForm: {
        
      },      
	  routerNo: '',
	  trackNo: '',
      workNo: '',
      userId:'',
	  userName:'',
	  siteId:'',
	  order:'',
	  orderCol:'',
	  startTime:formatDate(new Date(new Date().getTime()-86400 * 7 * 1000),'yyyy-MM-dd'),
      endTime:formatDate(new Date(),'yyyy-MM-dd'),
      activeName: '1',
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      trackList: [],
      dataListSelections: [],
      trackListLoading: false,
      // addOrUpdateVisible: false,
      trackListSelections: []
    }
  },
  components: {
    // AddOrUpdate
    // lineDetails
  },
  mounted() {
    this.getCompleteList();
  },

  methods: {
    // 刷新
    refresh() {
      this.getCompleteList()
    },
    searchReset() {
      Object.assign(this.$data, this.$options.data());
      this.getCompleteList();
    },
     async getUserInfobyname(id) {
      if (this.cache['username' + id] !== undefined) {
        return this.cache['username' + id]

      } else {
        var res = await this.$http({
          url: '/api/sys/user/query/page?page=1&limit=10&emplName=' + id,
          method: 'get'
        })
        this.cache['username' + id] = res
        return res
      }
    },
	async getUserInfo(id) {
      if (this.cache['user' + id] !== undefined) {
        return this.cache['user' + id]

      } else {
        var res = await this.$http({
          url: '/api/sys/user/query/page?page=1&limit=10&userAccount=' + id,
          method: 'get'
        })
        this.cache['user' + id] = res
        return res
      }
    },
	isBlank(val) {
      if (val === null || val === undefined || val === '') {
        return true
      } else {
        return false
      }
    },
	getCompleteList2() {
	   this.trackListLoading = true
	  completeService.getPage({
          'page': this.pageIndex,
          'limit': this.pageSize,
          'trackNo': this.trackNo,
          'routerNo': this.routerNo,
          'workNo': this.workNo,
          'startTime': this.startTime+' 00:00:00',
          'endTime': this.endTime+' 23:59:59',		  
          'userId': this.userId,
		  'order': this.order2,
		  'orderCol': this.orderCol2
        }
      ).then(async ({data}) => {
        if (data.status === 200 && data.data) {
          for (var i = 0; i < data.data.records.length; i++) {
            try {
              data.data.records[i].status = 0
              data.data.records[i].completedQty = 1 * data.data.records[i].completedQty
              data.data.records[i].actualHours = 1 * data.data.records[i].actualHours
              data.data.records[i].reportHours = 1 * data.data.records[i].reportHours              
              if (true) {     
				if (!this.isBlank(data.data.records[i].userId) && this.isBlank(data.data.records[i].emplName)) {
                 
				  
				  var res = await this.$http({
          url: '/api/sys/user/query/page?page=1&limit=10&userAccount=' + data.data.records[i].userId,
          method: 'get'
        })
		try {
		data.data.records[i].emplName = res.data.data.records[0].emplName
		}catch(e) {
		}
		
                }               
              } else {
                completeService.delete([data.data.records[i].id])
              }

            } catch (e) {
            }
          }
          this.trackList = data.data.records
          this.totalPage = data.data.total
        } else {
          this.completeList = []
          this.totalPage = 0
        }
		this.trackListLoading =false
      })
	},
    // 获取数据列表
    getCompleteList() { 
	
	 if(!this.isBlank(this.userId))
	  {
	  try{
	  this.$http({
          url: '/api/sys/user/query/page?page=1&limit=10&emplName=' + this.userId,
          method: 'get'
        }).then(({data}) => {
		try{
		  this.userId = data.data.records[0].userAccount
		  }catch(e2) {
		  }
		  this.getCompleteList2()
		  
		})
	  
	  }catch(e) {
	  }
	  }
	  else {
	  this.getCompleteList2()
	  }
	
     
    },
     export () {
	 },
     
    sizeChangeHandle(val) {
      this.pageSize = val
      this.pageIndex = 1
      this.getCompleteList()
    },

    // 当前页
    currentChangeHandle(val) {

      this.pageIndex = val
      this.getCompleteList()
    },
     // 行修改或保存
    editrow(row, index) {
      if (!row.status) {
        row.status = !row.status
        Vue.set(this.completeList, index, row)
      } else {
        
           this.$http({
        url: `/api/produce/trackcomplete/updatehours`,
        method: 'post',
        data: row
      }).then(({data}) => {
	      row.status = !row.status
	  })
        }
      
    },
	// 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val
    }
  }
}

</script>
