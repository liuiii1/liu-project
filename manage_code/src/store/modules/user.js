import toolUtil from '@/utils/toolUtil.js'
import config from '@/utils/config.js'
import http from '@/utils/http.js'
import default_avatar from "@/assets/img/avatar.png"

export default {
    namespaced: true,
    state:{
        session:{},
        username:'管理员',
        avatar:''
    },
    getters:{
        session(state){
            if(!state.session.id){
                return toolUtil.storageGet('back_session')?JSON.parse(toolUtil.storageGet('back_session')):{}
            }
            return state.session
        },
        avatar(state){
            let key;
            if(toolUtil.storageGet('sessionTable') == 'user'){
                key = 'headportrait'
            }
            let avatar = state.session[key]
            state.avatar = avatar
            return avatar?config.get().url+avatar:default_avatar
        },
    },
    actions:{
        async getSession({commit}){
            let res = await http.get(`${toolUtil.storageGet('sessionTable')}/session`)
            if(res.data.code==0){
                commit('set_session',res.data.data)
            }
            return res
        },
        async update({commit},data){
            let res = await http.post(`${toolUtil.storageGet('sessionTable')}/update`,data)
            if(res.data.code==0){
                commit('assign_session',data)
            }
            return res
        },
    },
    mutations:{
        set_session(state,data){
            state.session = data
            toolUtil.storageSet('back_session',JSON.stringify(state.session))
        },
        assign_session(state,data){
            Object.assign(state.session,JSON.parse(JSON.stringify(data)))
            toolUtil.storageSet('back_session',JSON.stringify(state.session))
        },
    }
}