<template>
  <el-scrollbar :style="{ height: $store.state.common.compAutoHeight + 'px' }">
    <div class="comp_padding">
      <el-form
        :inline="true"
        :model="dataForm"
        @keyup.enter.native="getDataList()"
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
                <el-input placeholder="转出工厂"  v-model="ToPlant" clearable
                          style="width: 180px"></el-input>
              </el-form-item>
			  
			   <el-form-item>
                <el-input placeholder="冲销状态"  v-model="ChargeOffFlag" clearable
                          style="width: 180px"></el-input>
              </el-form-item>
        </el-form-item>
                  <el-button @click="getDataList()" type="primary" icon="el-icon-search">查询</el-button>
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
		
        v-loading="trackListLoading"
		row-key="id"
        lazy
		:load="loadHistory"
		 :tree-props="{children: 'children', hasChildren: 'hasChildren'}">

      <el-table-column  type="index"
                         width="70"
                         align="center"
                         label="序号">
         
        </el-table-column>

      

        <el-table-column prop="certificate_no"
                         header-align="center"
                         width="80"
                         align="center"
                         label="合格证编号">

        </el-table-column>

        <el-table-column prop="drawing_no"
                         header-align="center"
                         flex
                         align="center"
                         label="图号">
      
        </el-table-column>

        <el-table-column prop="product_name"
                         header-align="center"
                         width="100"
                         align="center"
                         label="产品名称">
      
        </el-table-column>

      <el-table-column prop="material_name"
                         header-align="center"
                         width="70"
                         align="center"
                         label="材质">
   
        </el-table-column>

 <el-table-column prop="operation"
                         header-align="center"
                         width="70"
                         align="center"
                         label="工序要求">
       
        </el-table-column>
		
		 <el-table-column prop="work_no"
                         header-align="center"
                         width="70"
                         align="center"
                         label="工作号">
       
        </el-table-column>
 <el-table-column prop="number"
                         header-align="center"
                         width="70"
                         align="center"
                         label="数量">
       
        </el-table-column>

 <el-table-column prop="weight"
                         header-align="center"
                         width="70"
                         align="center"
                         label="单重">
    
        </el-table-column>
  <el-table-column prop="total_weight"
                         header-align="center"
                         width="90"
                         align="center"
                         label="总重">
     
        </el-table-column>

 <el-table-column prop="price"
                         header-align="center"
                         width="90"
                         align="center"
                         label="单价">
         
        </el-table-column>
 <el-table-column prop="totalprice"
                         header-align="center"
                         width="90"
                         align="center"
                         label="总价">
       
        </el-table-column>



</el-table>
      <el-pagination @size-change="sizeChangeHandle"
                     @current-change="currentChangeHandle"
                     :current-page="pageIndex"
                     :page-sizes="[10,100,1000,1000]"
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

export default {
  data() {
    return {
      dataForm: {
        
      },      
	  cache: {id: '1'}, //缓存数据
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
      pageSize: 9999,
      totalPage: 0,
      trackList: [],
	  dataList: [],
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
    this.getDataList();
  },

  methods: {
    // 刷新
    refresh() {
      this.getDataList()
    },
    searchReset() {
      Object.assign(this.$data, this.$options.data());
      this.getDataList();
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
	getDataList() {
	   this.trackListLoading = true
	   
	   this.$http({
          url: '/api/produce/jdbc/total?table=v_produce_certificate&where=1=1',
          method: 'get'
        }).then(t => {
		this.totalPage =t.data[0].count
	    this.$http({
          url: '/api/produce/jdbc/query?table=v_produce_certificate&where=1=1&limit=0,10',
          method: 'get'
        }).then(async ({data}) => {
        this.trackList = data
		console.log( this.trackList)
		 this.trackListLoading = false
      })
	  })
	},
    
	
	loadHistory(tree, treeNode, resolve) {
	
	  setTimeout(() => {
	    var children = new Array()
        for (var i = 0; i < this.dataList.length; i++) { 
		  
		     var isexist = false
		          
			 
			      if(tree.emplName ===this.dataList[i].emplName) {
				     isexist = true
				    
					 this.dataList[i].id = i
					 children.push(this.dataList[i])
			   
				  }
			 
			 
		  
		  
		  }
		  resolve(children)
		  }, 1000)
      },
     export () {
	 },
     
    sizeChangeHandle(val) {
      this.pageSize = val
      this.pageIndex = 1
      this.getDataList()
    },

    // 当前页
    currentChangeHandle(val) {

      this.pageIndex = val
      this.getDataList()
    },
    
	// 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val
    }
  }
}

</script>
