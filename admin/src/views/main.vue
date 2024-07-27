<template>
  <a-layout id="components-layout-demo-top-side-2">
    <the-header-vue></the-header-vue>
    <a-layout>
    <the-sider-vue></the-sider-vue>
      <a-layout style="padding: 0 24px 24px">

        <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
        >
         <router-view></router-view>
        </a-layout-content>
      </a-layout>
    </a-layout>
  </a-layout>
</template>
<script>
import { UserOutlined, LaptopOutlined, NotificationOutlined } from '@ant-design/icons-vue';
import { defineComponent, ref } from 'vue';
import TheHeaderVue from "@/components/the-header";
import TheSiderVue from "@/components/the-sider";
import axios from "axios";
import {notification} from "ant-design-vue";
import store from "@/store";
export default defineComponent({
  components: {
    TheSiderVue,
    TheHeaderVue,
    UserOutlined,
    LaptopOutlined,
    NotificationOutlined,
  },
  setup() {

    const count = ref(0);

    // axios.get("/member/member/count").then((response) => {
    //   let data = response.data;
    //   if (data.success) {
    //     count.value = data.content;
    //   } else {
    //     notification.error({ description: data.message });
    //   }
    // });

    return {
      count,
    };
  },
});
</script>
<style>

</style>