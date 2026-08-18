import { createApp } from 'vue'

import App from './App.vue'
import router from './router/index.ts'
import vuetify from "./plugins/vuetify.ts"

const app = createApp(App)
app.use(router)
app.use(vuetify)
app.mount('#app')
