using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.Windows.Forms;
using CefSharp;
using CefSharp.WinForms;
using System.Net;

namespace Xenxoid_Sync
{
    public partial class Form1 : Form
    {


        public Form1()
        {
            start_server();
            InitializeComponent();

        }

        void start_server()
        {
            ProcessStartInfo psi = new ProcessStartInfo();
            psi.FileName = "xenxoid_sync_server.exe";
            psi.UseShellExecute = false;
            psi.CreateNoWindow = true;
            Process proc = Process.Start(psi);
            proc.StartInfo.UseShellExecute = false;
        }

        public ChromiumWebBrowser browser;
        public void InitBrowser()
        {
            Cef.Initialize(new CefSettings());
            loadurl();
            browser = new ChromiumWebBrowser("http://127.0.0.1:5000/");
            this.Controls.Add(browser);
            browser.Dock = DockStyle.Fill;
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            InitBrowser();

        }

        void loadurl()
        {
            while (true)
            {
                if (RemoteFileExists("http://127.0.0.1:5000/"))
                {
                    break;
                }
                new System.Threading.ManualResetEvent(false).WaitOne(1000);
            }
        }

        private bool RemoteFileExists(string url)
        {
            try
            {
                //Creating the HttpWebRequest
                HttpWebRequest request = WebRequest.Create(url) as HttpWebRequest;
                //Setting the Request method HEAD, you can also use GET too.
                request.Method = "HEAD";
                //Getting the Web Response.
                HttpWebResponse response = request.GetResponse() as HttpWebResponse;
                //Returns TRUE if the Status code == 200
                response.Close();
                return (response.StatusCode == HttpStatusCode.OK);
            }
            catch
            {
                //Any exception will returns false.
                return false;
            }
        }

        private void Form1_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            Process.Start("CMD.exe", "taskkill /f /im xenxoid_sync_server.exe");
        }

       
    }
}
