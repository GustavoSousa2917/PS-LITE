<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { BalaoInformativoService } from '../repositories/BalaoInformativoService';
import type { BalaoInformativo } from '../types/BalaoInformativo';
import { useForm, useField } from 'vee-validate';
import { toTypedSchema } from '@vee-validate/zod';
import * as z from 'zod';

const props = defineProps<{
  processoId: number;
}>();

const baloes = ref<BalaoInformativo[]>([]);
const isLoading = ref(true);
const isFormVisible = ref(false);
const balaoEditandoId = ref<number | null>(null);

const validationSchema = toTypedSchema(
    z.object({
      titulo: z.string().min(1, 'O título é obrigatório'),
      mensagem: z.string().min(1, 'A mensagem é obrigatória'),
    })
);

const { handleSubmit, resetForm, isSubmitting } = useForm({ validationSchema });
const { value: titulo, errorMessage: tituloError } = useField<string>('titulo');
const { value: mensagem, errorMessage: mensagemError } = useField<string>('mensagem');

onMounted(async () => {
  await carregarBaloes();
});

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

// Prepara o formulário para edição
function abrirEdicao(balao: BalaoInformativo) {
  balaoEditandoId.value = balao.id || null;
  titulo.value = balao.titulo;
  mensagem.value = balao.mensagem;
  isFormVisible.value = true;
}

// Cancela e limpa o formulário
function cancelarFormulario() {
  resetForm();
  balaoEditandoId.value = null;
  isFormVisible.value = false;
}

const onSubmit = handleSubmit(async (values) => {
  try {
    const payload: BalaoInformativo = {
      titulo: values.titulo,
      mensagem: values.mensagem,
      processoSeletivo: { id: props.processoId }
    };

    if (balaoEditandoId.value) {
      await BalaoInformativoService.update(balaoEditandoId.value, payload);
    } else {
      await BalaoInformativoService.create(payload);
    }

    cancelarFormulario();
    await carregarBaloes();
  } catch (error) {
    console.error("Erro ao salvar balão", error);
  }
});

// Ação de Deletar
async function deletarBalao(id?: number) {
  if (!id) return;
  const confirmado = confirm('Tem certeza que deseja excluir este balão informativo?');
  if (!confirmado) return;

  try {
    await BalaoInformativoService.delete(id);
    await carregarBaloes();
  } catch (error) {
    console.error("Erro ao deletar balão:", error);
    alert('Erro ao excluir o balão. Verifique o console.');
  }
}
</script>

<template>
  <div>
    <!-- Botão Novo (Alinhado à direita, sem o título redundante) -->
    <div class="d-flex justify-end mb-4">
      <v-btn
          v-if="!isFormVisible"
          color="primary"
          prepend-icon="mdi-plus"
          variant="flat"
          @click="isFormVisible = true"
      >
        Novo Balão
      </v-btn>
    </div>

    <!-- Formulário Dinâmico -->
    <v-card v-if="isFormVisible" variant="outlined" class="pa-5 mb-6 bg-grey-lighten-4" style="border-color: #e5e7eb;">
      <h4 class="text-subtitle-1 font-weight-bold mb-4">
        {{ balaoEditandoId ? 'Editar Balão' : 'Criar Novo Balão' }}
      </h4>
      <v-form @submit.prevent="onSubmit">
        <v-text-field
            v-model="titulo"
            :error-messages="tituloError"
            label="Título do Balão"
            variant="outlined"
            color="primary"
            density="comfortable"
            bg-color="white"
        ></v-text-field>

        <v-textarea
            v-model="mensagem"
            :error-messages="mensagemError"
            label="Mensagem"
            variant="outlined"
            color="primary"
            density="comfortable"
            rows="3"
            bg-color="white"
        ></v-textarea>

        <div class="d-flex justify-end mt-2">
          <v-btn variant="text" color="grey-darken-2" class="mr-3" @click="cancelarFormulario">
            Cancelar
          </v-btn>
          <v-btn type="submit" color="primary" variant="flat" :loading="isSubmitting">
            Salvar
          </v-btn>
        </div>
      </v-form>
    </v-card>

    <!-- Estados da Lista -->
    <div v-if="isLoading" class="d-flex justify-center py-6">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </div>

    <v-alert
        v-else-if="baloes.length === 0"
        type="info"
        variant="tonal"
        class="mt-4"
    >
      Nenhum balão informativo cadastrado para este processo.
    </v-alert>

    <!-- Lista de Balões Estilizada e Azul -->
    <div v-else class="mt-4">
      <v-alert
          v-for="balao in baloes"
          :key="balao.id"
          variant="tonal"
          color="info"
          border="start"
          class="mb-3"
      >
        <div class="d-flex justify-space-between align-start w-100">
          <div>
            <div class="text-subtitle-1 font-weight-bold text-blue-darken-4">{{ balao.titulo }}</div>
            <div class="text-body-2 mt-1 text-blue-darken-3">{{ balao.mensagem }}</div>
          </div>
          <div class="d-flex align-center ml-4">
            <v-btn
                icon="mdi-pencil"
                variant="text"
                size="small"
                color="blue-darken-3"
                class="mr-1"
                @click="abrirEdicao(balao)"
            ></v-btn>
            <v-btn
                icon="mdi-delete"
                variant="text"
                size="small"
                color="error"
                @click="deletarBalao(balao.id)"
            ></v-btn>
          </div>
        </div>
      </v-alert>
    </div>
  </div>
</template>

<style scoped>
.gap-1 { gap: 4px; }
.gap-3 { gap: 12px; }
</style>