import './globals.css'
import type { Metadata } from 'next'
import { Inter } from 'next/font/google'
import Link from 'next/link'

const interFont = Inter({
    subsets: ['latin'],
    variable: '--font-inter',
})

export const metadata: Metadata = {
    title: 'Sistema da Rafael do Canto Advocacia e Socidade',
    description: 'Sistema de gerenciamento',
}

export default function RootLayout({
    children,
}: {
    children: React.ReactNode
}) {
    return (
        <html lang="pt-br">
            <body className={`${interFont.variable} font-inter w-screen h-screen bg-neutral-50 flex flex-col justify-start items-start transition-all overflow-hidden`}>
                <div className='w-full h-16 bg-blue-400 flex flex-row justify-between items-center p-2'>
                    <h1>
                        Logo
                    </h1>
                    <nav className='flex flex-row'>
                        <Link href="/" className='text-neutral-950 hover:text-blue-400 mx-2'>Início</Link>
                    </nav>
                </div>
                <div className='grow w-full h-full overflow-y-auto overflow-x-hidden'>
                    {children}
                </div>
            </body>
        </html>
    )
}
