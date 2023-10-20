export function generateRandomString(): string {
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    const format = [10, 21];

    for (let i = 0; i < 32; i++) {
        // Insert hyphens at the specified positions
        if (format.includes(i)) {
            result += '-';
        } else {
            const randomIndex = Math.floor(Math.random() * characters.length);
            result += characters[randomIndex];
        }
    }

    return result;
}