<script setup lang="ts">
import { useRoute } from 'vue-router';
import BaloesDoProcesso from '../../components/BaloesDoProcesso.vue';

const route = useRoute();
</script>

<template>
  <div class="pa-4">
    <h1>Detalhes do Processo (Em construção)</h1>

    <v-divider class="my-4"></v-divider>

    <BaloesDoProcesso :processoId="Number(route.params.id)" />
  </div>
</template>