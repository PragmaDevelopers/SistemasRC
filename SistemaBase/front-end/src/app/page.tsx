import { CogIcon, UserGroupIcon } from "@heroicons/react/24/solid";
import Image from "next/image";
import Link from "next/link";

export default function Page() {
    return (
        <main>
            <div>
                <form>
                    <input type="text" placeholder="Pesquisar Card" />
                </form>
                <details>
                    <summary></summary>
                    <div>
                        <Link href="">
                            <UserGroupIcon />
                            <h1>Usuários</h1>
                        </Link>
                    </div>
                </details>
                <details>
                    <summary></summary>
                    <div>
                        <Link href="">
                            <div>
                                <h1>Foto</h1>
                            </div>
                            <h1>Nome</h1>
                        </Link>
                        <Link href="">
                            <div>
                                <h1>Foto</h1>
                            </div>
                            <h1>Nome</h1>
                        </Link>
                        <Link href="">
                            <div>
                                <h1>Foto</h1>
                            </div>
                            <h1>Nome</h1>
                        </Link>
                        <Link href="">
                            <div>
                                <h1>Foto</h1>
                            </div>
                            <h1>Nome</h1>
                        </Link>
                    </div>
                </details>
                <div>
                    <CogIcon />
                    <h1>Configurações</h1>
                </div>
            </div>
            <div>
            </div>
        </main>
    );
}
