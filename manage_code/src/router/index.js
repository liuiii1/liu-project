	import {
		createRouter,
		createWebHashHistory
	} from 'vue-router'
	import discussagriculturalproductinformation from '@/views/discussagriculturalproductinformation/list'
	import news from '@/views/news/list'
	import address from '@/views/address/list'
	import productcategory from '@/views/productcategory/list'
	import supplier from '@/views/supplier/list'
	import orders from '@/views/orders/list'
	import agriculturalproductinformation from '@/views/agriculturalproductinformation/list'
    import menu_manage from '@/views/menu_manage/list'
	import storeup from '@/views/storeup/list'
	import user from '@/views/user/list'
	import config from '@/views/config/list'
	import users from '@/views/users/list'
	import supplierRegister from '@/views/supplier/register'
	import supplierCenter from '@/views/supplier/center'
	import caidata from '@/views/caidata/list'

export const routes = [{
		path: '/login',
		name: 'login',
		component: () => import('../views/login.vue')
	},{
		path: '/',
		name: '首页',
		component: () => import('../views/index'),
		children: [{
			path: '/',
			name: '首页1',
			component: () => import('../views/HomeView.vue'),
			meta: {
				affix: true
			}
		}, {
			path: '/updatepassword',
			name: '修改密码',
			component: () => import('../views/updatepassword.vue')
		}

		,{
			path: '/supplierCenter',
			name: '供应商个人中心',
			component: supplierCenter
		}
		,{
			path: '/discussagriculturalproductinformation',
			name: '农产品信息-评论',
			component: discussagriculturalproductinformation
		}
		,{
			path: '/news',
			name: '公告资讯',
			component: news
		}
		,{
			path: '/address',
			name: '地址',
			component: address
		}
		,{
			path: '/productcategory',
			name: '产品分类',
			component: productcategory
		}
		,{
			path: '/supplier',
			name: '供应商',
			component: supplier
		}
		,{
			path: '/orders',
			name: '订单管理',
			component: orders
		}
		,{
			path: '/agriculturalproductinformation',
			name: '农产品信息',
			component: agriculturalproductinformation
		}
        ,{
            path: '/menu',
            name: '菜单权限管理',
            component: menu_manage
        }
		,{
			path: '/storeup',
			name: '我的收藏',
			component: storeup
		}
		,{
			path: '/user',
			name: '用户',
			component: user
		}
		,{
			path: '/config',
			name: '轮播图',
			component: config
		}
		,{
			path: '/users',
			name: '管理员',
			component: users
		}
		,{
			path: '/caidata',
			name: '采摘数据列表',
			component: caidata
		}
		]
	},
	{
		path: '/supplierRegister',
		name: '供应商注册',
		component: supplierRegister
	},
]

const router = createRouter({
	history: createWebHashHistory(process.env.BASE_URL),
	routes
})

export default router
