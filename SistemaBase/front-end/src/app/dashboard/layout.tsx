'use client';

import { usePathname } from "next/navigation";
import { CalendarIcon, ChartPieIcon, CogIcon, ServerStackIcon, UserGroupIcon } from "@heroicons/react/24/solid";
import Image from "next/image";
import Link from "next/link";

interface BoardMenuEntryProps {
    href: string;
    name: string;
    picture: string;
}

function BoardMenuEntry(props: BoardMenuEntryProps) {
    return (
        <Link href={props.href} className="my-2 flex flex-row items-center">
            <Image src={props.picture} alt={props.name} width={640} height={640} className="w-8 aspect-square rounded-md overflow-hidden mr-2" />
            <h1>{props.name}</h1>
        </Link>
    );
}

export default function Layout({ children }: any) {
    const pathName: string = usePathname();
    const IconStyles: string = "w-8 aspect-square mr-2";

    return (
        <main className="w-full h-full flex flex-row items-start justify-between overflow-hidden">
            <div className="grow relative w-56 h-full flex flex-col justify-start items-start border-r-2 border-neutral-400">
                <form className="hidden">
                    <input type="text" placeholder="Pesquisar Card" />
                </form>
                <details className="p-2">
                    <summary>Seções</summary>
                    <div className="">
                        <Link href="/" className="my-1 flex flex-row items-center">
                            <UserGroupIcon className={IconStyles} />
                            <h1>Usuários</h1>
                        </Link>
                        <Link href="/" className="my-1 flex flex-row items-center">
                            <ServerStackIcon className={IconStyles} />
                            <h1>Areas de Trabalho</h1>
                        </Link>
                        <Link href="/" className="my-1 flex flex-row items-center">
                            <ChartPieIcon className={IconStyles} />
                            <h1>Relatorios</h1>
                        </Link>
                        <Link href="/" className="my-1 flex flex-row items-center">
                            <CalendarIcon className={IconStyles} />
                            <h1>Caléndario</h1>
                        </Link>
                    </div>
                </details>
                <div className="h-full flex flex-col justify-between overflow-hidden">
                    <details className="p-2 overflow-x-hidden overflow-y-auto">
                        <summary>Areas de Trabalho</summary>
                        <div className="">
                            <BoardMenuEntry href={`/dashboard/board/nome00`} picture="/84693449.png" name="nome" />
                            <BoardMenuEntry href={`/dashboard/board/nome01`} picture="/84693449.png" name="nome" />
                            <BoardMenuEntry href={`/dashboard/board/nome02`} picture="/84693449.png" name="nome" />
                            <BoardMenuEntry href={`/dashboard/board/nome03`} picture="/84693449.png" name="nome" />
                            <BoardMenuEntry href={`/dashboard/board/nome04`} picture="/84693449.png" name="nome" />
                            <BoardMenuEntry href={`/dashboard/board/nome05`} picture="/84693449.png" name="nome" />
                            <BoardMenuEntry href={`/dashboard/board/nome06`} picture="/84693449.png" name="nome" />
                            <BoardMenuEntry href={`/dashboard/board/nome07`} picture="/84693449.png" name="nome" />
                            <BoardMenuEntry href={`/dashboard/board/nome08`} picture="/84693449.png" name="nome" />
                            <BoardMenuEntry href={`/dashboard/board/nome09`} picture="/84693449.png" name="nome" />
                            <BoardMenuEntry href={`/dashboard/board/nome10`} picture="/84693449.png" name="nome" />
                            <BoardMenuEntry href={`/dashboard/board/nome11`} picture="/84693449.png" name="nome" />
                            <BoardMenuEntry href={`/dashboard/board/nome12`} picture="/84693449.png" name="nome" />
                            <BoardMenuEntry href={`/dashboard/board/nome13`} picture="/84693449.png" name="nome" />
                            <BoardMenuEntry href={`/dashboard/board/nome14`} picture="/84693449.png" name="nome" />
                            <BoardMenuEntry href={`/dashboard/board/nome15`} picture="/84693449.png" name="nome" />
                        </div>
                    </details>
                    <Link href="/" className="bg-neutral-50 p-2 flex flex-row items-center">
                        <CogIcon className={IconStyles} />
                        <h1>Configurações</h1>
                    </Link>
                </div>
            </div>
            <div className="grow w-full h-full overflow-y-hidden overflow-x-auto p-2 bg-neutral-200">
                {children}
            </div>
        </main>
    );
}
