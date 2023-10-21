"use client";
import Link from "next/link";
import { usePathname } from 'next/navigation';


export default function Header() {
    const currentPath: string = usePathname();
    if (currentPath != '/') {
        return (
            <div className='w-full h-16 flex flex-row justify-between items-center p-2'>
                <h1>
                    Logo
                </h1>
                <nav className='flex flex-row'>
                    <Link href="/" className='text-neutral-950 hover:text-blue-400 mx-2'>Início</Link>
                    <Link href="/dashboard" className='text-neutral-950 hover:text-blue-400 mx-2'>Dashboard</Link>
                    <Link href="/login" className='text-neutral-950 hover:text-blue-400 mx-2'>Cadastrar</Link>
                </nav>
            </div>
        );
    }
}
