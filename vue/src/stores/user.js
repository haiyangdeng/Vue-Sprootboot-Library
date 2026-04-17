import { ref, reactive, computed } from 'vue';
import { defineStore } from 'pinia';

export const useUserStore = defineStore(
    'user',
    () => {
        const token = ref('');
        const userInfo = reactive({
            id: null,
            username: '',
            email: '',
            address: '',
        });

        function setUser(data) {
            Object.assign(userInfo, data);
        }

        function setToken(newToken) {
            token.value = newToken;
        }

        function clearUser() {
            Object.assign(userInfo, { id: null, username: '', address: '', email: '' });
        }

        return { userInfo, token, setUser, setToken, clearUser };
    },
    {
        // 开启持久化
        persist: {
            storage: sessionStorage, // 切换为会话存储，默认是 localStorage
        },
    },
);
