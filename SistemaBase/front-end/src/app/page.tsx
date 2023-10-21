"use client";
import { useState } from "react";

export default function Page() {
    const [cadastrarSe, setCadastrarSe] = useState<boolean>(false);
    const switchCadastrarSe = () => setCadastrarSe(!cadastrarSe);
    return (
        <main>
            <div>
                <h1>Bem-Vindo(a)!</h1>
                <h2>Insira suas credenciais para acessar o sistema.</h2>
                <h3>Caso não esteja cadastrado, ultilize o botão <span>Cadastrar-se</span>.</h3>
            </div>
            <form>
                {cadastrarSe ? (
                    <div>
                        <input type="text" placeholder="Insira seu nome" className="bg-neutral-50" />
                        <input type="email" placeholder="Insira seu email" className="bg-neutral-50" />
                        <input type="text" placeholder="Insira sua nacionalidade" className="bg-neutral-50" />
                        <input type="text" placeholder="Insira seu genero" className="bg-neutral-50" />
                        <div className="flex flex-row justify-between items-center">
                            <input type="text" placeholder="Insira sua senha" className="bg-neutral-50" />
                            <input type="text" placeholder="Re-insira sua senha" className="bg-neutral-50" />
                        </div>
                    </div>
                ) : (
                    <div>

                    </div>
                )}
                <button type="submit">Entrar</button>
                <button type="button" onClick={switchCadastrarSe}>{cadastrarSe ? 'Voltar' : 'Cadastrar-se'}</button>
            </form>
        </main>
    );
}
