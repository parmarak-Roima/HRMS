import {
  QueryClient,
  QueryClientProvider,
} from '@tanstack/react-query'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
const queryClient = new QueryClient(
  {
    defaultOptions:{
      queries:{
        retry:false
      }
    }
  }
)

createRoot(document.getElementById('root')).render(
      <QueryClientProvider  client={queryClient} >
      <ReactQueryDevtools initialIsOpen={false} />
      <App />
      </ QueryClientProvider>
)
