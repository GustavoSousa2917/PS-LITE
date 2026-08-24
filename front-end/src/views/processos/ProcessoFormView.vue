<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useForm } from 'vee-validate';
import * as z from 'zod';
import { toTypedSchema } from '@vee-validate/zod';

const route = useRoute();
const router = useRouter();

const STATUS_OPTIONS = ['CADASTRADO', 'FINALIZADO', 'CANCELADO'];
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL;
const processosUrl = `${apiBaseUrl}/processo-seletivo`;

interface Processo {
  id?: number| string | undefined;
  nome: string;
  descricao?: string | null;
  qtdVagas: number;
  status: 'CADASTRADO' | 'FINALIZADO' | 'CANCELADO' | string;
}

const processo = ref<Processo | null>(null);
const loading = ref(false);
const saving = ref(false);
const erro = ref('');
const notFound = ref(false);
const currentMode = computed(() => {
  const modeFromProp = route.query.mode || route.params.mode;
  if (modeFromProp === 'edit') return 'edit';
  if (modeFromProp === 'detail') return 'detail';
  if (route.params.id) return 'edit';
  return 'create';
});

const schema = toTypedSchema(
  z.object({
    id: z.number().optional(),
    nome: z.string().trim().min(1, 'Nome é obrigatório.'),
    descricao: z.string().trim().optional().default(''),
    qtdVagas: z.coerce.number({ invalid_type_error: 'Quantidade de vagas é obrigatória.' }).min(0, 'Quantidade de vagas deve ser maior ou igual a 0.'),
    status: z.enum(['CADASTRADO', 'FINALIZADO', 'CANCELADO'], {
      required_error: 'Status é obrigatório.',
      invalid_type_error: 'Status inválido.'
    })
  })
);

const {
  handleSubmit,
  errors,
  setValues,
  defineField,
} = useForm({
  validationSchema: schema,

 initialValues: {
  id: undefined,
  nome: '',
  descricao: '',
  qtdVagas: 0,
  status: 'CADASTRADO', // era 'ATIVO'
},
});

const [nome, nomeProps] = defineField('nome');
const [descricao, descricaoProps] = defineField('descricao');
const [qtdVagas, qtdVagasProps] = defineField('qtdVagas');
const [status, statusProps] = defineField('status');

const formatStatus = (value: string | number) => {
  const labels: Record<string, string> = {
    CADASTRADO: 'Cadastrado',
    FINALIZADO: 'Finalizado',
    CANCELADO: 'Cancelado'
  };
  return labels[String(value)] ?? String(value);
};

const redirectToListagem = () => {
  router.push('/processos');
};

const redirectToDetalhe = (id: string) => {
  router.push(`/processos/${id}`);
};

const parseJson = async (response: Response) => {
  if (response.status === 204) return null;

  const text = await response.text();
  return text ? JSON.parse(text) : null;
};

const loadProcesso = async () => {
  const id = route.params.id;
  if (!id) return;

  loading.value = true;
  erro.value = '';
  notFound.value = false;

  try {
    const response = await fetch(`${processosUrl}/${id}`);

    if (!response.ok) {
      if (response.status === 404) {
        notFound.value = true;
        return;
      }

      throw new Error('Não foi possível carregar o processo.');
    }

    const data = await parseJson(response);
    processo.value = data;

    if (currentMode.value === 'edit') {
      setValues({
        id: data.id,
        nome: data.nome || '',
        descricao: data.descricao || '',
        qtdVagas: Number(data.qtdVagas ?? 0),
        status: data.status || 'CADASTRADO'
      });
    }
  } catch (error) {
    erro.value = error instanceof Error ? error.message : 'Erro ao carregar processo.';
  } finally {
    loading.value = false;
  }
};

const submitForm = handleSubmit(async (values: any) => {
  const payload = {
    id: route.params.id ? Number(route.params.id) : undefined,
    nome: values.nome,
    descricao: values.descricao || '',
    qtdVagas: Number(values.qtdVagas),
    status: values.status
  };

  saving.value = true;
  erro.value = '';

  try {
    const id = route.params.id;
    const method = currentMode.value === 'edit' && id ? 'PUT' : 'POST';
    const url = id && currentMode.value === 'edit' ? `${processosUrl}/${id}` : processosUrl;

    const response = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    });

    const data = await parseJson(response);

    if (!response.ok) {
      const message = data?.message || data?.error || 'Não foi possível salvar o processo.';
      throw new Error(message);
    }

    const savedId = data?.id || id || data?.processoId;
    if (savedId) {
      redirectToDetalhe(savedId);
      return;
    }

    redirectToListagem();
  } catch (error) {
    erro.value = (error instanceof Error ? error.message : String(error)) || 'Erro ao salvar o processo.';
  } finally {
    saving.value = false;
  }
});

const isDetailMode = computed(() => currentMode.value === 'detail');

onMounted(() => {
  if (currentMode.value === 'edit' || currentMode.value === 'detail') {
    loadProcesso();
  }
});

watch(
  () => route.params.id,
  () => {
    if (currentMode.value === 'edit' || currentMode.value === 'detail') {
      loadProcesso();
    }
  }
);
</script>

<template>
  <section class="processo-view">
    <div v-if="isDetailMode" class="detail-page">
      <header class="page-header">
        <div>
          <p class="eyebrow">Processo</p>
          <h1>{{ processo?.nome || 'Detalhe do processo' }}</h1>
        </div>
        <v-button class="primary-button" type="button" @click="router.push(`/processos/${route.params.id}/editar?mode=edit`)">
          Editar
        </v-button>
      </header>

      <div v-if="loading" class="state-box">Carregando processo...</div>
      <div v-else-if="notFound" class="state-box error-box">Processo não encontrado.</div>
      <div v-else-if="erro" class="state-box error-box">{{ erro }}</div>
      <div v-else-if="processo" class="detail-content">
        <div class="card-grid">
          <div class="info-card">
            <span class="label">Nome</span>
            <strong>{{ processo.nome }}</strong>
          </div>

          <div class="info-card">
            <span class="label">Vagas</span>
            <strong>{{ processo.qtdVagas }}</strong>
          </div>

          <div class="info-card wide">
            <span class="label">Status</span>
            <strong>{{ formatStatus(processo.status) }}</strong>
          </div>

          <div class="info-card wide description-card">
            <span class="label">Descrição</span>
            <p>{{ processo.descricao || 'Sem descrição informada.' }}</p>
          </div>
        </div>

        <div class="baloes-panel">
          <h2>Balões (D)</h2>
          <div class="baloes-placeholder">
            <div class="balao-slot">Reservado para balões</div>
            <div class="balao-slot">Reservado para balões</div>
            <div class="balao-slot">Reservado para balões</div>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="form-page">
      <header class="page-header">
        <div>
          <p class="eyebrow">Processo</p>
          <h1>{{ currentMode === 'edit' ? 'Editar processo' : 'Novo processo' }}</h1>
        </div>
      </header>

      <form class="processo-form" @submit.prevent="submitForm">
        <div class="field-group">
          <label for="nome">Nome</label>
          <input
  id="nome"
  v-model="nome"
  v-bind="nomeProps"
  type="text"
  placeholder="Digite o nome do processo"
/>
          <small v-if="errors.nome" class="error-message">{{ errors.nome }}</small>
        </div>

        <div class="field-group">
          <label for="descricao">Descrição</label>
          <textarea id="descricao" v-model="descricao" v-bind="descricaoProps" rows="4" placeholder="Descreva o processo" />
          <small v-if="errors.descricao" class="error-message">{{ errors.descricao }}</small>
        </div>

        <div class="field-row">
          <div class="field-group">
            <label for="qtdVagas">Quantidade de vagas</label>
            <input id="qtdVagas" v-model.number="qtdVagas" v-bind="qtdVagasProps" type="number" min="0" />
            <small v-if="errors.qtdVagas" class="error-message">{{ errors.qtdVagas }}</small>
          </div>

          <div class="field-group">
            <label for="status">Status</label>
            <select id="status" v-model="status" v-bind="statusProps">
              <option v-for="option in STATUS_OPTIONS" :key="option" :value="option">
                {{ formatStatus(option) }}
              </option>
            </select>
            <small v-if="errors.status" class="error-message">{{ errors.status }}</small>
          </div>
        </div>

        <div v-if="erro" class="form-alert error-box">{{ erro }}</div>

        <div class="actions">
          <button type="button" class="secondary-button" @click="redirectToListagem">
            Cancelar
          </button>
          <button type="submit" class="primary-button" :disabled="saving">
            {{ saving ? 'Salvando...' : currentMode === 'edit' ? 'Salvar alterações' : 'Criar processo' }}
          </button>
  
        </div>
      </form>
    </div>
  </section>
</template>

<style scoped>
.processo-view {
  width: 100%;
  max-width: 960px;
  margin: 0 auto;
  padding: 24px;
  box-sizing: border-box;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
}

.eyebrow {
  margin: 0 0 8px;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #6b7280;
}

h1,
h2 {
  margin: 0;
  color: #111827;
}

.processo-form {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.field-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

label {
  font-weight: 600;
  color: #374151;
}

input,
textarea,
select {
  width: 100%;
  box-sizing: border-box;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 10px 12px;
  font: inherit;
  background: #fff;
  color: #111827;
}

input:focus,
textarea:focus,
select:focus {
  outline: 2px solid rgba(37, 99, 235, 0.2);
  border-color: #2563eb;
}

textarea {
  resize: vertical;
}

.error-message,
.error-box {
  color: #b91c1c;
}

.error-box {
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 8px;
  padding: 10px 12px;
  margin-top: 12px;
}

.primary-button,
.secondary-button {
  border: 0;
  border-radius: 8px;
  padding: 10px 16px;
  font-weight: 600;
  cursor: pointer;
}

.primary-button {
  background: #2563eb;
  color: #fff;
}

.primary-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.secondary-button {
  background: #e5e7eb;
  color: #111827;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.detail-content {
  display: grid;
  gap: 24px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.info-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 18px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-card.wide {
  grid-column: span 2;
}

.description-card p {
  margin: 0;
  color: #374151;
  line-height: 1.5;
}

.label {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #6b7280;
}

.baloes-panel {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
}

.baloes-placeholder {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.balao-slot {
  min-height: 120px;
  border: 1px dashed #cbd5e1;
  border-radius: 12px;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
  text-align: center;
  padding: 12px;
}

.state-box {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 24px;
  color: #374151;
}

@media (max-width: 640px) {
  .page-header,
  .field-row,
  .card-grid,
  .baloes-placeholder {
    grid-template-columns: 1fr;
    display: grid;
  }

  .page-header {
    align-items: flex-start;
  }

  .actions {
    flex-direction: column-reverse;
  }

  .primary-button,
  .secondary-button {
    width: 100%;
  }
}
</style>
