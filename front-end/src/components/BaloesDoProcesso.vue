<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { BalaoInformativoService } from '../repositories/BalaoInformativoService';
import type { BalaoInformativo } from '../types/BalaoInformativo';

// Importações novas para Validação
import { useForm, useField } from 'vee-validate';
import { toTypedSchema } from '@vee-validate/zod';
import * as z from 'zod';

const props = defineProps<{
  processoId: number;
}>();

const baloes = ref<BalaoInformativo[]>([]);
const isLoading = ref(true);

// 1. Variável para abrir/fechar o formulário
const isFormVisible = ref(false);

// 2. Criar as regras de validação (Zod)
const validationSchema = toTypedSchema(
    z.object({
      titulo: z.string().min(1, 'O título é obrigatório'),
      mensagem: z.string().min(1, 'A mensagem é obrigatória'),
    })
);

// 3. Configurar o formulário (Vee-Validate)
const { handleSubmit, resetForm, isSubmitting } = useForm({
  validationSchema,
});

// Ligar os campos às variáveis
const { value: titulo, errorMessage: tituloError } = useField<string>('titulo');
const { value: mensagem, errorMessage: mensagemError } = useField<string>('mensagem');

onMounted(async () => {
  carregarBaloes();
});

// Função separada para recarregar a lista facilmente
async function carregarBaloes() {
  try {
    isLoading.value = true;
    baloes.value = await BalaoInformativoService.listByProcesso(props.processoId);
  } catch (error) {
    console.error("Erro ao buscar balões:", error);
  } finally {
    isLoading.value = false;
  }
}

// 4. Ação de Salvar o Balão
const onSubmit = handleSubmit(async (values) => {
  try {
    // Monta o objeto no formato do contrato
    const novoBalao: BalaoInformativo = {
      titulo: values.titulo,
      mensagem: values.mensagem,
      processoSeletivo: { id: props.processoId }
    };

    // Envia para o service
    await BalaoInformativoService.create(novoBalao);

    // Limpa o form, fecha a tela e recarrega a lista
    resetForm();
    isFormVisible.value = false;
    await carregarBaloes();

  } catch (error) {
    console.error("Erro ao salvar balão", error);
  }
});
</script>

<template>
  <v-card class="mt-4" variant="outlined">
    <v-card-title class="d-flex justify-space-between align-center">
      Balões Informativos
      <!-- Botão para abrir o formulário -->
      <v-btn color="primary" @click="isFormVisible = !isFormVisible">
        {{ isFormVisible ? 'Cancelar' : 'Novo Balão' }}
      </v-btn>
    </v-card-title>

    <v-card-text>
      <!-- Formulário de Criação -->
      <v-form v-if="isFormVisible" @submit.prevent="onSubmit" class="mb-4">
        <v-text-field
            v-model="titulo"
            label="Título do Balão"
            :error-messages="tituloError"
        ></v-text-field>

        <v-textarea
            v-model="mensagem"
            label="Mensagem"
            :error-messages="mensagemError"
            rows="3"
        ></v-textarea>

        <v-btn type="submit" color="success" :loading="isSubmitting">
          Salvar
        </v-btn>
      </v-form>

      <v-divider v-if="isFormVisible" class="my-4"></v-divider>

      <!-- Lista de Balões (Igual ao anterior) -->
      <div v-if="isLoading" class="text-center">Carregando balões...</div>
      <div v-else-if="baloes.length === 0" class="text-center text-grey">Nenhum balão.</div>
      <v-list v-else>
        <v-list-item v-for="balao in baloes" :key="balao.id">
          <v-list-item-title class="font-weight-bold">{{ balao.titulo }}</v-list-item-title>
          <v-list-item-subtitle>{{ balao.mensagem }}</v-list-item-subtitle>
        </v-list-item>
      </v-list>
    </v-card-text>
  </v-card>
</template>