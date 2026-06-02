Add-Type @"
using System;
using System.Runtime.InteropServices;
public class TopMost {
    [DllImport("user32.dll")]
    public static extern bool SetWindowPos(IntPtr hWnd, IntPtr hWndInsertAfter, int X, int Y, int cx, int cy, uint uFlags);

    static readonly IntPtr HWND_TOPMOST = new IntPtr(-1);
    static readonly IntPtr HWND_NOTOPMOST = new IntPtr(-2);

    public static void MakeTopMost(IntPtr hWnd) {
        // Set as topmost (appears above everything)
        SetWindowPos(hWnd, HWND_TOPMOST, 0, 0, 0, 0, 0x0001 | 0x0002 | 0x0040);
        // Then remove topmost so it behaves normally after
        SetWindowPos(hWnd, HWND_NOTOPMOST, 0, 0, 0, 0, 0x0001 | 0x0002 | 0x0040);
    }
}
"@

$procs = Get-Process chrome -ErrorAction SilentlyContinue | Where-Object { $_.MainWindowHandle -ne 0 }
foreach ($p in $procs) {
    [TopMost]::MakeTopMost($p.MainWindowHandle)
}
