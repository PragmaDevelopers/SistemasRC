import crypto from 'crypto';

export function hashString(input: string): string {
    const hash = crypto.createHash('sha256');
    hash.update(input, 'utf8');
    return hash.digest('hex');
}

